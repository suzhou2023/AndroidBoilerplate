/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include "log_util.h"
#include "egl_util.h"

/**
 * 通过在native层手动配置EGL环境，来操作屏幕
 */
extern "C"
void egl(JNIEnv *env, jobject obj, jobject surface) {

    // 配置EGL环境
    EGLConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;

    // 屏幕设置颜色
    glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
}
