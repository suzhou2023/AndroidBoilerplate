//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_ANDROID_LOG_H
#define ANDROIDBOILERPLATE_LOGUTIL_H

#include <android/log.h>


#define  LOG_TAG "BBT_GLES"


#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)


#endif //ANDROIDBOILERPLATE_ANDROID_LOG_H
