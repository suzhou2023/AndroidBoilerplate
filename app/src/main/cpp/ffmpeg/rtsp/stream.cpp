/**
 *  author : suzhou
 *  date : 2023/10/21 
 *  description : 打开rtsp流
 */

#include "stream.h"

extern "C" {
#include "libavdevice/avdevice.h"
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}

#include <ctime>
#include <thread>
#include "log_callback.h"
#include "gl.h"


void openRtspStream(GLContext *glContext, const char *url) {
    // 必要的变量定义
    AVFormatContext *format_ctx = nullptr;
    AVStream *video_stream = nullptr;
    AVCodecContext *codec_ctx = nullptr;
    const AVCodec *codec;
    AVPacket *packet = nullptr;
    AVFrame *frame = nullptr;

    // android日志打印
    av_log_set_callback(log_callback_null);

    // 申请一个AVFormatContext
    format_ctx = avformat_alloc_context();

    AVDictionary *av_dict = nullptr;
    // 设置缓存大小 1024000 byte
    av_dict_set(&av_dict, "buffer_size", "1024000", 0);
    // 设置超时时间 60s
    av_dict_set(&av_dict, "stimeout", "60000000", 0);
    // 设置最大延时 3s
    av_dict_set(&av_dict, "max_delay", "3000000", 0);
    // 设置打开方式 tcp/udp
    av_dict_set(&av_dict, "rtsp_transport", "udp", 0);

    // 打开流
    int ret = avformat_open_input(&format_ctx, url, nullptr, &av_dict);
    if (ret < 0) {
        LOGE("avformat_open_input error: %s", av_err2str(ret));
        goto FREE;
    }

    // 读取流信息
    ret = avformat_find_stream_info(format_ctx, nullptr);
    if (ret < 0) {
        LOGE("avformat_find_stream_info error: %s", av_err2str(ret));
        goto FREE;
    }

    // 找出视频流
    for (int index = 0; index < format_ctx->nb_streams; index++) {
        auto codec_type = format_ctx->streams[index]->codecpar->codec_type;
        if (codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream = format_ctx->streams[index];
            break;
        }
    }

    // 未找到视频流
    if (video_stream == nullptr) {
        LOGE("No video stream found.");
        goto FREE;
    }

    // 寻找对应的解码器
    codec = avcodec_find_decoder(video_stream->codecpar->codec_id);
    if (codec == nullptr) {
        LOGE("avcodec_find_decoder failed.");
        goto FREE;
    }

    // 申请一个AVCodecContext
    codec_ctx = avcodec_alloc_context3(codec);
    if (codec_ctx == nullptr) {
        LOGE("avcodec_alloc_context3 failed.");
        goto FREE;
    }

    // 利用codec参数填充AVCodecContext相关字段
    ret = avcodec_parameters_to_context(codec_ctx, video_stream->codecpar);
    if (ret < 0) {
        LOGE("avcodec_parameters_to_context error: %s", av_err2str(ret));
        goto FREE;
    }

    // 利用codec初始化AVCodecContext
    ret = avcodec_open2(codec_ctx, codec, nullptr);
    if (ret < 0) {
        LOGE("avcodec_open2 error: %s", av_err2str(ret));
        goto FREE;
    }

    // 申请一个AVPacket，调用av_packet_free释放
    packet = av_packet_alloc();
    // 申请一个AVFrame，调用av_frame_free释放
    frame = av_frame_alloc();
    // 循环读取视频帧
    while (true) {
        struct timeval start{}, end{};
        gettimeofday(&start, nullptr);

        // 读取一帧视频编码数据，成功后会设置packet->buf，需调用av_packet_unref释放
        ret = av_read_frame(format_ctx, packet);
        if (ret < 0) {
            LOGE("av_read_frame error: %s", av_err2str(ret));
            continue;
        }

        if (packet->stream_index != video_stream->index) {
            continue;
        }

        // 向解码器发送原始的packet数据作为解码器输入，返回值：
        // AVERROR(EAGAIN) - 需调用avcodec_receive_frame读取输出，然后重新发送输入数据
        ret = avcodec_send_packet(codec_ctx, packet);
        if (ret == 0) {
            LOGD("avcodec_send_packet success");
        } else if (ret == AVERROR(EAGAIN)) {
            LOGE("avcodec_send_packet error: %s", av_err2str(ret));
        } else if (ret == AVERROR_EOF) {
            LOGE("avcodec_send_packet error: AVERROR_EOF");
            break;
        } else {
            LOGE("avcodec_send_packet error: %s", av_err2str(ret));
        }

        // 得到解码后的数据帧，返回值：
        // AVERROR(EAGAIN) - 输出数据不可得，需向解码器发送新的输入数据
        ret = avcodec_receive_frame(codec_ctx, frame);
        if (ret == 0) {
            LOGD("avcodec_receive_frame success");
            LOGD("is keyframe = %d", (frame->flags & AV_FRAME_FLAG_KEY) != 0);
            LOGD("width-height = %d-%d", frame->width, frame->height);
            LOGD("linesize[0] = %d", frame->linesize[0]);
            LOGD("linesize[1] = %d", frame->linesize[1]);
            LOGD("linesize[2] = %d", frame->linesize[2]);
            // opengl绘制
            gl_drawYuv(glContext, frame->linesize[0], frame->height, frame->data[0], frame->data[1],
                       frame->data[2]);
            // 解码、渲染一帧的时间
            gettimeofday(&end, nullptr);
            LOGD("time diff = %ld", (end.tv_usec - start.tv_usec) / 1000);
        } else if (ret == AVERROR(EAGAIN)) {
            LOGE("avcodec_receive_frame error: %s", av_err2str(ret));
        } else if (ret == AVERROR_EOF) {
            LOGE("avcodec_receive_frame error: AVERROR_EOF");
            break;
        } else {
            LOGE("avcodec_receive_frame error: %s", av_err2str(ret));
        }

        // 解引用buffer，reset其它字段
        av_frame_unref(frame);
        // 解引用buffer，reset其它字段
        av_packet_unref(packet);

        // 线程休眠
        std::this_thread::sleep_for(std::chrono::milliseconds(15));
    }

    FREE:
    if (frame != nullptr) {
        av_frame_free(&frame);
    }
    if (packet != nullptr) {
        av_packet_free(&packet);
    }
    if (codec_ctx != nullptr) {
        avcodec_free_context(&codec_ctx);
    }
    if (av_dict != nullptr) {
        av_dict_free(&av_dict);
    }
    if (format_ctx != nullptr) {
        avformat_free_context(format_ctx);
    }
}










































