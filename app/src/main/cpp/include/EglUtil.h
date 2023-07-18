/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_EGLUTIL_H
#define ANDROIDBOILERPLATE_EGLUTIL_H

#include <EGL/egl.h>
#include <android/native_window_jni.h>
#include <cstdlib>
#include "LogUtil.h"
#include "ShaderUtil.h"


typedef struct {
    EGLDisplay display;
    EGLSurface eglSurface;
} EglConfigInfo;

static EglConfigInfo g_EglConfigInfo;

static GLint configEGL(JNIEnv *env, jobject surface) {
    g_EglConfigInfo.display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (g_EglConfigInfo.display == EGL_NO_DISPLAY) {
        LOGE("EGL get display failed.");
        return -1;
    }

    if (EGL_TRUE != eglInitialize(g_EglConfigInfo.display, nullptr, nullptr)) {
        LOGE("EGL initialize failed");
        return -1;
    }

    EGLConfig eglConfig;
    EGLint configNum;
    EGLint configSpec[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_NONE
    };

    if (EGL_TRUE != eglChooseConfig(g_EglConfigInfo.display, configSpec, &eglConfig,
                                    1, &configNum)) {
        LOGE("EGL choose config failed.");
        return -1;
    }

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    g_EglConfigInfo.eglSurface = eglCreateWindowSurface(g_EglConfigInfo.display, eglConfig,
                                                        nativeWindow, nullptr);
    // todo: 是在这里释放吗？对后续代码貌似没有影响
    ANativeWindow_release(nativeWindow);
    if (g_EglConfigInfo.eglSurface == EGL_NO_SURFACE) {
        LOGE("EGL create window surface failed.");
        return -1;
    }

    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };

    EGLContext context = eglCreateContext(g_EglConfigInfo.display, eglConfig,
                                          EGL_NO_CONTEXT, ctxAttr);
    if (context == EGL_NO_CONTEXT) {
        LOGE("EGL create context failed.");
        return -1;
    }

    if (EGL_TRUE != eglMakeCurrent(g_EglConfigInfo.display, g_EglConfigInfo.eglSurface,
                                   g_EglConfigInfo.eglSurface, context)) {
        LOGE("EGL make current failed.");
        return -1;
    }

    return 0;
}

#endif //ANDROIDBOILERPLATE_EGLUTIL_H
