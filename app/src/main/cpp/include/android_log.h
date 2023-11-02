//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_ANDROID_LOG_H
#define ANDROIDBOILERPLATE_ANDROID_LOG_H

#include <android/log.h>


#define  LOG_TAG "===BBT-NATIVE==="


#define  LOGV(...)  __android_log_print(ANDROID_LOG_VERBOSE,LOG_TAG,__VA_ARGS__)

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG, "=============================================================="); \
                    __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__); \
                    __android_log_print(ANDROID_LOG_INFO,LOG_TAG, "==============================================================")

#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, "=============================================================="); \
                    __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__); \
                    __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG, "==============================================================")

#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, "=============================================================="); \
                    __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__); \
                    __android_log_print(ANDROID_LOG_ERROR,LOG_TAG, "==============================================================")


#endif //ANDROIDBOILERPLATE_ANDROID_LOG_H
