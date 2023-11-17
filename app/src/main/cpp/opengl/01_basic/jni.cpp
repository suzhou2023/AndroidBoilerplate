/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include "03_vbo.h"
#include "07_texture.h"
#include "08_yuv.h"
#include "08_yuv2.h"
#include "10_rgb2nv12.h"
#include "10_rgb2vyuy.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basic_jni_Jni_nativeApiTest(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    vbo(env, thiz, glContext);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basic_jni_Jni_nativeTexture(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject bitmap) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);
    texture(env, glContext, bitmap);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basic_jni_Jni_nativeLoadYuv(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    loadYuv(env, thiz, glContext);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basic_jni_Jni_nativeLoadYuv2(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    loadYuv2(env, thiz, glContext);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basic_jni_Jni_nativeRgb2nv12(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject bitmap, jobject callback) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    rgb2nv12(env, thiz, glContext, bitmap, callback);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basic_jni_Jni_nativeRgb2vyuy(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject bitmap, jobject callback) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    rgb2vyuy(env, thiz, glContext, bitmap, callback);
}





























