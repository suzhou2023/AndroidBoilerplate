/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <android/native_window_jni.h>
#include <cstring>
#include "log_util.h"
#include "01_native_window.h"
#include "02_egl.h"
#include "03_vbo.h"
#include "04_ebo.h"
#include "05_vao.h"
#include "06_vao_vbo_ebo.h"
#include "07_texture.h"
#include "08_yuv.h"
#include "09_fbo.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeApiTest(
        JNIEnv *env, jobject thiz, jobject surface) {
//    native_window(env, thiz, surface);
//    egl(env,thiz,surface);
//    vbo(env,thiz,surface);
//    ebo(env,thiz,surface);
//    vao(env,thiz,surface);
    vao_vbo_ebo(env, thiz, surface);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeLoadYuv(
        JNIEnv *env, jobject thiz, jobject surface, jobject asset_manager) {
    loadYuv(env, thiz, surface, asset_manager);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativeTexture(
        JNIEnv *env, jobject thiz, jobject surface, jobject bitmap) {
    texture(env, thiz, surface, bitmap);
//    fbo(env, surface, bitmap);
}

