//
// Created by sz on 2023/7/13.
//

#include "native_render_jni.h"
#include "log_util.h"
#include "jni.h"
#include "GlRenderContext.h"


#define NATIVE_RENDER_CLASS_NAME "com/bbt2000/boilerplate/demos/opengles/NativeRender"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL native_OnInit(JNIEnv *env, jobject instance) {
    GlRenderContext::getInstance();
}

JNIEXPORT void JNICALL native_OnDestroy(JNIEnv *env, jobject instance) {
    GlRenderContext::destroyInstance();
}

JNIEXPORT void JNICALL native_OnSurfaceCreated(JNIEnv *env, jobject instance) {
    GlRenderContext::getInstance()->onSurfaceCreated();
}

JNIEXPORT void JNICALL native_OnSurfaceChanged
        (JNIEnv *env, jobject instance, jint width, jint height) {
    GlRenderContext::getInstance()->onSurfaceChanged(width, height);
}

JNIEXPORT void JNICALL native_OnDrawFrame(JNIEnv *env, jobject instance) {
    GlRenderContext::getInstance()->onDrawFrame();
}

#ifdef __cplusplus
}
#endif

static JNINativeMethod g_RenderMethods[] = {
        {"native_OnInit",           "()V",   (void *) (native_OnInit)},
        {"native_OnDestroy",        "()V",   (void *) (native_OnDestroy)},
        {"native_OnSurfaceCreated", "()V",   (void *) (native_OnSurfaceCreated)},
        {"native_OnSurfaceChanged", "(II)V", (void *) (native_OnSurfaceChanged)},
        {"native_OnDrawFrame",      "()V",   (void *) (native_OnDrawFrame)},
};

static int RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, 5) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static void UnregisterNativeMethods(JNIEnv *env, const char *className) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return;
    }
    if (env != NULL) {
        env->UnregisterNatives(clazz);
    }
}

extern "C" jint JNI_OnLoad(JavaVM *jvm, void *p) {
    jint jniRet = JNI_ERR;
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return jniRet;
    }

    jint regRet = RegisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME, g_RenderMethods);
    if (regRet != JNI_TRUE) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

extern "C" void JNI_OnUnload(JavaVM *jvm, void *p) {
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    UnregisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME);
}

