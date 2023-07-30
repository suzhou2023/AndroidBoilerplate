/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_EGL_UTIL_H
#define ANDROIDBOILERPLATE_EGL_UTIL_H

#include <EGL/egl.h>
#include <android/native_window_jni.h>
#include <cstdlib>
#include "log_util.h"
#include "shader_util.h"


typedef struct {
    EGLDisplay display;
    EGLSurface eglSurface;
    EGLContext context;
} EGLConfigInfo;


/**
 * createContext
 * @param env
 * @param surface
 * @return
 */
static EGLConfigInfo *createContext(JNIEnv *env, jobject surface, EGLContext shareContext = EGL_NO_CONTEXT) {
    EGLConfigInfo *p_EGLConfigInfo = static_cast<EGLConfigInfo *>(malloc(sizeof(EGLConfigInfo)));
    if (p_EGLConfigInfo == nullptr) return nullptr;

    p_EGLConfigInfo->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (p_EGLConfigInfo->display == EGL_NO_DISPLAY) {
        LOGE("EGL get display failed.");
        free(p_EGLConfigInfo);
        return nullptr;
    }

    if (eglInitialize(p_EGLConfigInfo->display, nullptr, nullptr) != EGL_TRUE) {
        LOGE("EGL initialize failed");
        eglTerminate(p_EGLConfigInfo->display);
        free(p_EGLConfigInfo);
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

    if (eglChooseConfig(
            p_EGLConfigInfo->display,
            configSpec,
            &eglConfig,
            1,
            &configNum) != EGL_TRUE) {
        LOGE("EGL choose config failed.");
        eglTerminate(p_EGLConfigInfo->display);
        free(p_EGLConfigInfo);
        return nullptr;
    }

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    p_EGLConfigInfo->eglSurface = eglCreateWindowSurface(
            p_EGLConfigInfo->display,
            eglConfig,
            nativeWindow,
            nullptr);
    // todo: 是在这里释放吗？对后续代码貌似没有影响
    ANativeWindow_release(nativeWindow);
    if (p_EGLConfigInfo->eglSurface == EGL_NO_SURFACE) {
        LOGE("EGL create window surface failed.");
        eglTerminate(p_EGLConfigInfo->display);
        free(p_EGLConfigInfo);
        return nullptr;
    }

    const EGLint ctxAttr[] = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};

    p_EGLConfigInfo->context = eglCreateContext(
            p_EGLConfigInfo->display,
            eglConfig,
            shareContext,
            ctxAttr
    );
    if (p_EGLConfigInfo->context == EGL_NO_CONTEXT) {
        LOGE("EGL create context failed.");
        eglTerminate(p_EGLConfigInfo->display);
        free(p_EGLConfigInfo);
        return nullptr;
    }

    return p_EGLConfigInfo;
}

/**
 * makeCurrent
 * @param p_EGLConfigInfo
 * @return
 */
static EGLBoolean makeCurrent(EGLConfigInfo *p_EGLConfigInfo) {
    EGLBoolean ret = eglMakeCurrent(
            p_EGLConfigInfo->display,
            p_EGLConfigInfo->eglSurface,
            p_EGLConfigInfo->eglSurface,
            p_EGLConfigInfo->context
    );
    if (ret != EGL_TRUE) {
        LOGE("EGL make current failed.");
        return EGL_FALSE;
    }
    return EGL_TRUE;
}


/**
 * todo: 弃用
 * @param env
 * @param surface
 * @param p_EGLConfigInfo
 * @return
 */
static GLint configEGL(JNIEnv *env, jobject surface, EGLConfigInfo *p_EGLConfigInfo) {
    p_EGLConfigInfo->display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (p_EGLConfigInfo->display == EGL_NO_DISPLAY) {
        LOGE("EGL get display failed.");
        return -1;
    }

    if (EGL_TRUE != eglInitialize(p_EGLConfigInfo->display, nullptr, nullptr)) {
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

    if (EGL_TRUE != eglChooseConfig(p_EGLConfigInfo->display, configSpec, &eglConfig,
                                    1, &configNum)) {
        LOGE("EGL choose config failed.");
        return -1;
    }

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    p_EGLConfigInfo->eglSurface = eglCreateWindowSurface(p_EGLConfigInfo->display, eglConfig,
                                                         nativeWindow, nullptr);
    // todo: 是在这里释放吗？对后续代码貌似没有影响
    ANativeWindow_release(nativeWindow);
    if (p_EGLConfigInfo->eglSurface == EGL_NO_SURFACE) {
        LOGE("EGL create window surface failed.");
        return -1;
    }

    const EGLint ctxAttr[] = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};

    p_EGLConfigInfo->context = eglCreateContext(p_EGLConfigInfo->display, eglConfig,
                                                EGL_NO_CONTEXT, ctxAttr);
    if (p_EGLConfigInfo->context == EGL_NO_CONTEXT) {
        LOGE("EGL create context failed.");
        eglDestroySurface(p_EGLConfigInfo->display, p_EGLConfigInfo->eglSurface);
        return -1;
    }

    EGLBoolean ret = eglMakeCurrent(p_EGLConfigInfo->display, p_EGLConfigInfo->eglSurface,
                                    p_EGLConfigInfo->eglSurface, p_EGLConfigInfo->context);
    if (ret != EGL_TRUE) {
        LOGE("EGL make current failed.");
        eglDestroyContext(p_EGLConfigInfo->display, p_EGLConfigInfo->context);
        eglDestroySurface(p_EGLConfigInfo->display, p_EGLConfigInfo->eglSurface);
        eglTerminate(p_EGLConfigInfo->display);
        return -1;
    }
    LOGI("Config EGL success.");

    return 0;
}


/**
 * destroyEGL
 * @param p_EGLConfigInfo
 */
static void destroyEGL(EGLConfigInfo *p_EGLConfigInfo) {
    if (p_EGLConfigInfo == nullptr) return;
    eglDestroySurface(p_EGLConfigInfo->display, p_EGLConfigInfo->eglSurface);
    eglDestroyContext(p_EGLConfigInfo->display, p_EGLConfigInfo->context);
    eglTerminate(p_EGLConfigInfo->display);
    free(p_EGLConfigInfo);
}

#endif //ANDROIDBOILERPLATE_EGL_UTIL_H
