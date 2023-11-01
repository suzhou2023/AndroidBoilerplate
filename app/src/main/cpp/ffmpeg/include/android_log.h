/**
 *  author : suzhou
 *  date : 2023/10/2 
 *  description : 
 */
#ifndef BBT_COLPOSCOPE_PATIENT_LOG_UTIL_H
#define BBT_COLPOSCOPE_PATIENT_LOG_UTIL_H


extern "C" {
#include "libavutil/log.h"
}

#include <android/log.h>
#include <string.h>


#define  LOG_TAG    "FFMPEG-ANDROID"

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


static void log_callback_null(void *ptr, int level, const char *fmt, va_list vl) {
    static int print_prefix = 1;
    static char prev[1024];
    char line[1024];
    static int is_atty;

    av_log_format_line(ptr, level, fmt, vl, line, sizeof(line), &print_prefix);

    strcpy(prev, line);

    if (level <= AV_LOG_WARNING) {
        LOGE("%s", line);
    } else {
        LOGI("%s", line);
    }
}


#endif //BBT_COLPOSCOPE_PATIENT_LOG_UTIL_H




































