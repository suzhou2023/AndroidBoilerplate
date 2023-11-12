/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include "GLContext.h"
#include "03_vbo.h"


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

//    texture(env, glContext, bitmap);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeLoadYuv(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

//    loadYuv(env, thiz, glContext);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeLoadYuv2(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

//    loadYuv2(env, thiz, glContext);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeRgb2nv12(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject bitmap, jobject callback) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

//    rgb2nv12(env, thiz, glContext, bitmap, callback);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeRgb2vyuy(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject bitmap, jobject callback) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

//    rgb2vyuy(env, thiz, glContext, bitmap, callback);
}





















