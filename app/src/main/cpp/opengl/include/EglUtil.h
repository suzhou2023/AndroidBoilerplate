/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_EGLUTIL_H
#define ANDROIDBOILERPLATE_EGLUTIL_H


#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <GLES3/gl3.h>
#include <android/native_window_jni.h>
#include <cstdlib>
#include "GLContext.h"
#include "LogUtil.h"
#include "ShaderUtil.h"


class EglUtil {
public:
    EGLBoolean createContext(GLContext *glContext, EGLContext shareContext = EGL_NO_CONTEXT) {
        glContext->eglDisplay = eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (glContext->eglDisplay == EGL_NO_DISPLAY) {
            LOGE("eglGetDisplay failed.");
            return EGL_FALSE;
        }

        if (eglInitialize(glContext->eglDisplay, nullptr, nullptr) != EGL_TRUE) {
            LOGE("eglInitialize failed");
            eglTerminate(glContext->eglDisplay);
            glContext->eglDisplay = nullptr;
            return EGL_FALSE;
        }

        EGLConfig eglConfig;
        EGLint configNum;
        EGLint configSpec[] = {
                EGL_RED_SIZE, 8,
                EGL_GREEN_SIZE, 8,
                EGL_BLUE_SIZE, 8,
                EGL_ALPHA_SIZE, 8,
                EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
                EGL_RECORDABLE_ANDROID, 1,
                EGL_NONE
        };

        if (eglChooseConfig(
                glContext->eglDisplay,
                configSpec,
                &eglConfig,
                1,
                &configNum) != EGL_TRUE) {
            LOGE("eglChooseConfig failed.");
            eglTerminate(glContext->eglDisplay);
            glContext->eglDisplay = nullptr;
            return EGL_FALSE;
        } else {
            glContext->eglConfig = eglConfig;
        }

        const EGLint ctxAttr[] = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE};
        glContext->eglContext = eglCreateContext(
                glContext->eglDisplay,
                eglConfig,
                shareContext,
                ctxAttr
        );
        if (glContext->eglContext == EGL_NO_CONTEXT) {
            LOGE("createContext failed.");
            eglTerminate(glContext->eglDisplay);
            glContext->eglDisplay = nullptr;
            return EGL_FALSE;
        }

        LOGI("createContext success: %p", glContext->eglContext);
        return EGL_TRUE;
    }

    EGLBoolean eglCreateSurface(JNIEnv *env, GLContext *glContext, jobject surface, EGLint index) {
        if (index >= sizeof(glContext->eglSurface) / sizeof(glContext->eglSurface[0])) {
            return EGL_FALSE;
        }

        // 销毁之前的EGLSurface
        if (glContext->eglSurface[index] != nullptr) {
            EGLBoolean ret = eglDestroySurface(glContext->eglDisplay, glContext->eglSurface[index]);
            if (ret == EGL_TRUE) {
                LOGI("eglDestroySurface success: %p", glContext->eglSurface[index]);
            }
            glContext->eglSurface[index] = nullptr;
        }

        ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
        glContext->eglSurface[index] = eglCreateWindowSurface(
                glContext->eglDisplay,
                glContext->eglConfig,
                nativeWindow,
                nullptr);

        ANativeWindow_release(nativeWindow);
        if (glContext->eglSurface[index] == EGL_NO_SURFACE) {
            LOGE("eglCreateSurface failed.");
            return EGL_FALSE;
        }

        LOGI("eglCreateSurface success: %p", glContext->eglSurface[index]);
        return EGL_TRUE;
    }

    EGLBoolean makeCurrent(GLContext *glContext, EGLSurface eglSurface) {
        EGLBoolean ret = eglMakeCurrent(
                glContext->eglDisplay,
                eglSurface,
                eglSurface,
                glContext->eglContext
        );

        if (ret != EGL_TRUE) {
            LOGE("makeCurrent failed.");
            return EGL_FALSE;
        }

        return EGL_TRUE;
    }
};

static EglUtil eglUtil = EglUtil();


#endif //ANDROIDBOILERPLATE_EGLUTIL_H








