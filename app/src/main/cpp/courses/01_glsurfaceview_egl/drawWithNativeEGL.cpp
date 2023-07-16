/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include "LogUtil.h"
#include "EglUtil.h"

/**
 * 通过在native层手动配置EGL环境，来操作屏幕
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__101_1glsurfaceview_1egl_SurfaceViewTest_drawWithNativeEGL(
        JNIEnv *env, jobject obj,
        jobject surface, jint color) {

    // 配置EGL环境
    EglConfigInfo *p_eglConfigInfo = configEGL(env, surface);

    // 屏幕设置颜色
    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    eglSwapBuffers(p_eglConfigInfo->display, p_eglConfigInfo->eglSurface);
}