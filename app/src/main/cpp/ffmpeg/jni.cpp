/**
 *  author : suzhou
 *  date : 2023/10/31
 *  description : 
 */

#include <jni.h>
#include "rtsp/stream.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_openStream(JNIEnv *env, jobject thiz, jstring url) {
    const char *url_str = env->GetStringUTFChars(url, nullptr);

    openStream(url_str);

    env->ReleaseStringUTFChars(url, url_str);
}



































