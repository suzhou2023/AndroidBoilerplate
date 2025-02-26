/**
 *  author : sz
 *  date : 2023/11/16
 *  description : 
 */


#include "FFContext.h"

#include <ctime>
#include <thread>
#include "gl.h"


// 连续读取图像帧
void FFContext::readFrames(GLContext *glContext) {
    while (true) {
        int ret = readOneFrame(glContext);
        if (ret == AVERROR_EOF) {
            break;
        }
    }
}


// 读取一帧解码图像
int FFContext::readOneFrame(GLContext *glContext) {

    // 起始时间
    struct timeval start{};
    gettimeofday(&start, nullptr);

    // 申请一个AVPacket，需调用av_packet_free释放
    if (packet == nullptr) {
        packet = av_packet_alloc();
    }

    // 申请一个AVFrame，需调用av_frame_free释放
    if (frame == nullptr) {
        frame = av_frame_alloc();
    }

    // 读取一帧视频编码数据，成功后会设置packet->buf，需调用av_packet_unref释放
    int ret = av_read_frame(format_ctx, packet);
    if (ret < 0) {
        LOGE("av_read_frame error: %s", av_err2str(ret));
        return ret;
    }

    // 只处理视频流
    if (packet->stream_index != video_stream->index) {
        av_packet_unref(packet);
        return 0;
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
        return AVERROR_EOF;
    } else {
        LOGE("avcodec_send_packet error: %s", av_err2str(ret));
    }

    // 获取解码后的数据帧，返回值：
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
        gl_drawYuv(glContext, frame->linesize[0], frame->height, frame->data[0], frame->data[1], frame->data[2]);
    } else if (ret == AVERROR(EAGAIN)) {
        LOGE("avcodec_receive_frame error: %s", av_err2str(ret));
    } else if (ret == AVERROR_EOF) {
        LOGE("avcodec_receive_frame error: AVERROR_EOF");
    } else {
        LOGE("avcodec_receive_frame error: %s", av_err2str(ret));
    }

    // 解引用buffer，reset其它字段
    av_frame_unref(frame);
    // 解引用buffer，reset其它字段
    av_packet_unref(packet);

    // 结束时间
    struct timeval end{};
    gettimeofday(&end, nullptr);
    long time_diff = (end.tv_sec * 1000000 + end.tv_usec) - (start.tv_sec * 1000000 + start.tv_usec);
    LOGD("time_diff = %ld", time_diff / 1000);

    // 线程休眠时间
    int frame_rate = video_stream->avg_frame_rate.num;
    if (frame_rate <= 0 || frame_rate > 60) frame_rate = 30;
    int wait_time = 1000 / frame_rate - time_diff / 1000 - 1;
    if (wait_time > 0 && wait_time < 1000 / frame_rate) {
        std::this_thread::sleep_for(std::chrono::milliseconds(wait_time));
    }

    return true;
}


// 打开rtsp流
bool FFContext::openRtspStream(const char *url) {
    // 申请一个AVFormatContext
    format_ctx = avformat_alloc_context();

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
        return false;
    }

    // 读取流信息
    ret = avformat_find_stream_info(format_ctx, nullptr);
    if (ret < 0) {
        LOGE("avformat_find_stream_info error: %s", av_err2str(ret));
        return false;
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
        return false;
    }

    // 寻找对应的解码器
    const AVCodec *codec = avcodec_find_decoder(video_stream->codecpar->codec_id);
    if (codec == nullptr) {
        LOGE("avcodec_find_decoder failed.");
        return false;
    }

    // 申请一个AVCodecContext
    codec_ctx = avcodec_alloc_context3(codec);
    if (codec_ctx == nullptr) {
        LOGE("avcodec_alloc_context3 failed.");
        return false;
    }

    // 利用codec参数填充AVCodecContext相关字段
    ret = avcodec_parameters_to_context(codec_ctx, video_stream->codecpar);
    if (ret < 0) {
        LOGE("avcodec_parameters_to_context error: %s", av_err2str(ret));
        return false;
    }

    // 利用codec初始化AVCodecContext
    ret = avcodec_open2(codec_ctx, codec, nullptr);
    if (ret < 0) {
        LOGE("avcodec_open2 error: %s", av_err2str(ret));
        return false;
    }

    return true;
}



































