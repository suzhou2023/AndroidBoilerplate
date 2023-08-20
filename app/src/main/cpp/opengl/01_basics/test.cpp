/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include "03_vbo.h"
#include "08_yuv.h"
#include "08_yuv2.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeApiTest(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    vbo(env, thiz, glContext);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeTexture(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject bitmap) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);
    // todo
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeLoadYuv(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    loadYuv(env, thiz, glContext);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeLoadYuv2(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    loadYuv2(env, thiz, glContext);
}





