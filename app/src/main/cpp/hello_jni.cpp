//
// Created by sz on 2023/7/13.
//
#include <cstdio>
#include <string>
#include "jni.h"
#include "android/log.h"

#define TAG "hello_jni"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_opengles_JniDemo_sayHello(JNIEnv *env, jobject thiz) {
    printf("Native say hello\n");
    __android_log_print(ANDROID_LOG_VERBOSE, TAG, "Native say hello");
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_bbt2000_boilerplate_demos_opengles_JniDemo_stringFromNative(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}