/**
 *  author : suzhou
 *  date : 2023/10/31
 *  description : 
 */

#include <jni.h>
#include "common/FFContext.h"
#include "gl/GLContext.h"
#include "gl/gl_util.h"


extern "C"
JNIEXPORT jlong JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_createFFContext(JNIEnv *env, jobject thiz) {
    auto *ffContext = new FFContext();
    return reinterpret_cast<jlong>(ffContext);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_destroyFFContext(JNIEnv *env, jobject thiz, jlong ff_context) {
    if (ff_context <= 0) return;
    auto *ffContext = reinterpret_cast<FFContext *>(ff_context);

    delete ffContext;
}


extern "C"
JNIEXPORT jboolean JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_openRtspStream(JNIEnv *env, jobject thiz, jlong ff_context,
                                                                 jstring url) {
    if (ff_context <= 0) return false;
    auto *ffContext = reinterpret_cast<FFContext *>(ff_context);

    const char *url_str = env->GetStringUTFChars(url, nullptr);
    bool ret = ffContext->openRtspStream(url_str);
    env->ReleaseStringUTFChars(url, url_str);

    return ret;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_glSetMatrix(JNIEnv *env, jobject thiz, jlong ff_context,
                                                              jlong gl_context, jint program_index, jint window_w,
                                                              jint window_h, jint scale_type, jboolean rotate) {
    if (ff_context <= 0) return;
    if (gl_context <= 0) return;
    auto *ffContext = reinterpret_cast<FFContext *>(ff_context);
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    int frame_w = ffContext->codec_ctx->width;
    int frame_h = ffContext->codec_ctx->height;

    gl_setMatrix(glContext->program[program_index], frame_w, frame_h, window_w, window_h, scale_type, rotate);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_readFrames(JNIEnv *env, jobject thiz, jlong ff_context,
                                                             jlong gl_context) {
    if (ff_context <= 0) return;
    if (gl_context <= 0) return;
    auto *ffContext = reinterpret_cast<FFContext *>(ff_context);
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    ffContext->readFrames(glContext);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_bbt2000_boilerplate_demos_ffmpeg_jni_Jni_readOneFrame(JNIEnv *env, jobject thiz, jlong ff_context,
                                                               jlong gl_context) {
    if (ff_context <= 0) return false;
    if (gl_context <= 0) return false;
    auto *ffContext = reinterpret_cast<FFContext *>(ff_context);
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    return ffContext->readOneFrame(glContext);
}





































