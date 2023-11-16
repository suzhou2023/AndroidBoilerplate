/**
 *  author : sz
 *  date : 2023/11/16
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_FFCONTEXT_H
#define ANDROIDBOILERPLATE_FFCONTEXT_H


extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
}

#include "gl/GLContext.h"
#include "log_callback.h"


class FFContext {
public:
    // 打开rtsp流
    bool openRtspStream(const char *url);

    // 连续读取图像帧
    void readFrames(GLContext *glContext);

    // 读取一帧解码后的图像
    int readOneFrame(GLContext *glContext);


    FFContext() {
        av_log_set_callback(log_callback_null);
    };

    ~FFContext() {
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
        LOGI("FFContext destroyed.");
    };

    AVFormatContext *format_ctx{nullptr};
    AVDictionary *av_dict{nullptr};
    AVCodecContext *codec_ctx{nullptr};
    AVPacket *packet{nullptr};
    AVFrame *frame{nullptr};
    AVStream *video_stream{nullptr}; // 视频流
};


#endif //ANDROIDBOILERPLATE_FFCONTEXT_H


























