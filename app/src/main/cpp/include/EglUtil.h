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

EglConfigInfo *configEGL(JNIEnv *env, jobject surface) {
    EglConfigInfo *p_eglConfigInfo = static_cast<EglConfigInfo *>(malloc(sizeof(EglConfigInfo)));

    p_eglConfigInfo->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (p_eglConfigInfo->display == EGL_NO_DISPLAY) {
        LOGE("EGL get display failed.");
        return nullptr;
    }

    if (EGL_TRUE != eglInitialize(p_eglConfigInfo->display, nullptr, nullptr)) {
        LOGE("EGL initialize failed");
        return nullptr;
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

    if (EGL_TRUE != eglChooseConfig(p_eglConfigInfo->display, configSpec, &eglConfig,
                                    1, &configNum)) {
        LOGE("EGL choose config failed.");
        return nullptr;
    }

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    p_eglConfigInfo->eglSurface = eglCreateWindowSurface(p_eglConfigInfo->display, eglConfig,
                                                         nativeWindow, nullptr);
    // todo: 是在这里释放吗？对后续代码貌似没有影响
    ANativeWindow_release(nativeWindow);
    if (p_eglConfigInfo->eglSurface == EGL_NO_SURFACE) {
        LOGE("EGL create window surface failed.");
        return nullptr;
    }

    const EGLint ctxAttr[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE
    };

    EGLContext context = eglCreateContext(p_eglConfigInfo->display, eglConfig,
                                          EGL_NO_CONTEXT, ctxAttr);
    if (context == EGL_NO_CONTEXT) {
        LOGE("EGL create context failed.");
        return nullptr;
    }

    if (EGL_TRUE != eglMakeCurrent(p_eglConfigInfo->display, p_eglConfigInfo->eglSurface,
                                   p_eglConfigInfo->eglSurface, context)) {
        LOGE("EGL make current failed.");
        return nullptr;
    }
    return p_eglConfigInfo;
}

#endif //ANDROIDBOILERPLATE_EGLUTIL_H
