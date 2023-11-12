/**
 *  author : suzhou
 *  date : 2023/10/21 
 *  description : 
 */

extern "C" {
#include "libavdevice/avdevice.h"
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}

#include "stream.h"
#include "log_callback.h"


void openStream(const char *url) {

    // 必要的变量定义
    AVFormatContext *format_ctx = nullptr;
    AVStream *video_stream = nullptr;
    AVCodecContext *codec_ctx = nullptr;
    const AVCodec *codec = nullptr;
    AVPacket *packet = nullptr;
    AVFrame *frame = nullptr;

    // android日志输出
    av_log_set_callback(log_callback_null);
    // todo:是否需要
    avdevice_register_all();
    // todo:是否需要
    avformat_network_init();

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

    if (video_stream == nullptr) {
        LOGE("No video stream found.");
        goto FREE;
    }

    codec = avcodec_find_decoder(video_stream->codecpar->codec_id);
    if (codec == nullptr) {
        LOGE("No decoder found.");
        goto FREE;
    }

    // 申请一个AVCodecContext
    codec_ctx = avcodec_alloc_context3(codec);
    if (codec_ctx == nullptr) {
        LOGE("Allocate AVCodecContext failed.");
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

    packet = av_packet_alloc();
    frame = av_frame_alloc();
    // 循环读取视频帧
    while (true) {
        // 返回一帧视频编码数据(对于音频不止一帧)
        ret = av_read_frame(format_ctx, packet);
        if (ret < 0) {
            LOGE("av_read_frame error: %s", av_err2str(ret));
            continue;
        }

        // 如果是视频流，处理其解码、拿帧等
        if (packet->stream_index == video_stream->index) {
            // 为codec提供原始的packet数据
            ret = avcodec_send_packet(codec_ctx, packet);
            if (ret < 0) {
                LOGE("avcodec_send_packet error: %s", av_err2str(ret));
            }

            // 解引用buffer，reset其它字段
            av_packet_unref(packet);

            // 返回解码后的数据
            ret = avcodec_receive_frame(codec_ctx, frame);
            if (ret == 0) {
                LOGD("pts = %ld", frame->pts);
                LOGD("format = %d", frame->format); // AVPixelFormat枚举
                LOGD("key frame flag = %d", frame->flags & AV_FRAME_FLAG_KEY);
                LOGD("size = %dx%d", frame->width, frame->height);
                LOGD("linesize[0] = %d", frame->linesize[0]);
                LOGD("linesize[1] = %d", frame->linesize[1]);
                LOGD("linesize[2] = %d", frame->linesize[2]);


                LOGD("buf[0]->size = %d", frame->buf[0]->size);
                LOGD("buf[1]->size = %d", frame->buf[1]->size);
                LOGD("buf[2]->size = %d", frame->buf[2]->size);
            } else {
                LOGE("avcodec_receive_frame error: %s", av_err2str(ret));
            }

            

            // 解引用buffer，reset其它字段
            av_frame_unref(frame);

            break;
        }
    }

    FREE:
    if (frame != nullptr) {
        av_frame_free(&frame);
    }
    if (packet != nullptr) {
        av_packet_free(&packet);
    }
    if (av_dict != nullptr) {
        av_dict_free(&av_dict);
    }
    if (format_ctx != nullptr) {
        avformat_free_context(format_ctx);
    }
}










































