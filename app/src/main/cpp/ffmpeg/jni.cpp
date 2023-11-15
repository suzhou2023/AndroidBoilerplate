/**
 *  author : suzhou
 *  date : 2023/10/31
 *  description : 
 */

#include <jni.h>
#include "rtsp/stream.h"
#include "gl/GLContext.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_openStream(JNIEnv *env, jobject thiz, jlong gl_context, jstring url) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    const char *url_str = env->GetStringUTFChars(url, nullptr);
    openStream(glContext, url_str);
    env->ReleaseStringUTFChars(url, url_str);
}



































