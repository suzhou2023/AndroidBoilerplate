/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_JNIUTIL_H
#define ANDROIDBOILERPLATE_JNIUTIL_H

#include <jni.h>


extern "C" const char *GetFullJavaClassNameForNativeRegister();
extern "C" JNINativeMethod *GetJniNativeMethodTableForRegister();
extern "C" jint GetJniNativeMethodTableLengthForRegister();

extern "C" int RegisterNativeMethods(JNIEnv *env, const char *className,
                                     JNINativeMethod *methods, jint nMethods) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, nMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

extern "C" void UnregisterNativeMethods(JNIEnv *env, const char *className) {
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

    const char *class_name = GetFullJavaClassNameForNativeRegister();
    JNINativeMethod *methods = GetJniNativeMethodTableForRegister();
    jint nMethods = GetJniNativeMethodTableLengthForRegister();
    jint regRet = RegisterNativeMethods(env, class_name, methods, nMethods);
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

    const char *class_name = GetFullJavaClassNameForNativeRegister();
    UnregisterNativeMethods(env, class_name);
}


#endif //ANDROIDBOILERPLATE_JNIUTIL_H
