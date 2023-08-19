/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <android/native_window_jni.h>
#include <cstring>
#include "01_native_window.h"
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
//    texture(env, thiz, surface, bitmap);
    fbo(env, surface, bitmap);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1basics_SurfaceViewTest_nativePrintGLSL(
        JNIEnv *env, jobject thiz, jobject asset_manager) {

    AAssetManager *assetManager = AAssetManager_fromJava(env, asset_manager);
    AAsset *aAsset = AAssetManager_open(assetManager, "shader/v_shader_simple.glsl", AASSET_MODE_BUFFER);
    off_t len = AAsset_getLength(aAsset);
    unsigned char buf[len + 1];
    buf[len] = '\0';
    AAsset_read(aAsset, buf, len);
    LOGD("buf = \n%s", buf);
}



