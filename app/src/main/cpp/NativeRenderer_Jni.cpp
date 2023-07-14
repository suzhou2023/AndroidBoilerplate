//
// Created by sz on 2023/7/13.
//

#include "LogUtil.h"
#include "jni.h"
#include "NativeRenderer.h"
#include "courses/01_triangle/01_triangle.h"


#ifdef __cplusplus
extern "C" {
#endif


JNIEXPORT void JNICALL native_OnSurfaceCreated(JNIEnv *env, jobject instance) {
    NativeRenderer::getInstance()->onSurfaceCreated();
}

JNIEXPORT void JNICALL native_OnSurfaceChanged
        (JNIEnv *env, jobject instance, jint width, jint height) {
    NativeRenderer::getInstance()->onSurfaceChanged(width, height);
}

JNIEXPORT void JNICALL native_OnDrawFrame(JNIEnv *env, jobject instance) {
    NativeRenderer::getInstance()->onDrawFrame();
}

#ifdef __cplusplus
}
#endif

#define NATIVE_RENDER_CLASS_NAME "com/bbt2000/boilerplate/demos/opengles/NativeRenderer"

static JNINativeMethod g_RenderMethods[] = {
        {"native_OnSurfaceCreated", "()V",   (void *) (native_OnSurfaceCreated)},
        {"native_OnSurfaceChanged", "(II)V", (void *) (native_OnSurfaceChanged)},
        {"native_OnDrawFrame",      "()V",   (void *) (native_OnDrawFrame)},
};

static int RegisterNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, 3) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static void UnregisterNativeMethods(JNIEnv *env, const char *className) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return;
    }
    env->UnregisterNatives(clazz);
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

