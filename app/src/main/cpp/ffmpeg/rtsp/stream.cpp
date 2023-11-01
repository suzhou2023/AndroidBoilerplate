/**
 *  author : suzhou
 *  date : 2023/10/21 
 *  description : 
 */

extern "C" {
#include "libavdevice/avdevice.h"
#include "libavformat/avformat.h"
}

#include "LogUtil.h"
#include "stream.h"
#include "android_log.h"
#include "libavcodec/avcodec.h"


void openStream(const char *url) {
    // android日志输出
    av_log_set_callback(log_callback_null);

    // todo:是否需要
    avdevice_register_all();

    // todo:是否需要
    avformat_network_init();

    AVFormatContext *format_ctx = avformat_alloc_context();

    AVDictionary *av_dict = nullptr;
    // 设置缓存大小 1024000 byte
    av_dict_set(&av_dict, "buffer_size", "1024000", 0);
    // 设置超时时间 60s
    av_dict_set(&av_dict, "stimeout", "60000000", 0);
    // 设置最大延时 3s
    av_dict_set(&av_dict, "max_delay", "3000000", 0);
    // 设置打开方式 tcp/udp
    av_dict_set(&av_dict, "rtsp_transport", "udp", 0);

    int ret = avformat_open_input(&format_ctx, url, nullptr, &av_dict);
    LOGD("avformat_open_input, ret = %d", ret);
    if (ret < 0) {
        LOGE("avformat_open_input error: %d", ret);
    }

    ret = avformat_find_stream_info(format_ctx, nullptr);
    if (ret < 0) {
        LOGE("avformat_find_stream_info error: %d", ret);
    }

    AVCodecContext *codec_ctx = nullptr;
    AVStream *video_stream = nullptr;
    for (int index = 0; index < format_ctx->nb_streams; index++) {
        auto codec_type = format_ctx->streams[index]->codecpar->codec_type;
        if (codec_type == AVMEDIA_TYPE_VIDEO) {
            video_stream = format_ctx->streams[index];
            break;
        }
    }

    if (video_stream == nullptr) {
        LOGE("No video stream found.");
    }

    av_dict_free(&av_dict);
    avformat_free_context(format_ctx);
}






























