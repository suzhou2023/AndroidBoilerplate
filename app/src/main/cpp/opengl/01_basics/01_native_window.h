/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <android/native_window_jni.h>
#include <cstring>
#include "android_log.h"

/**
 * opengles绘图都要通过EGL层才能显示在屏幕上
 * 这个例子用来测试，不走EGL，通过直接操作native window的方式控制屏幕显示
 * 这里用的是memset简单测试一下，操作native window的buffer
 */
extern "C"
void native_window(JNIEnv *env, jobject obj, jobject surface) {

    // 拿到java层surface对应的native window
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (nativeWindow == nullptr) {
        LOGE("Acquire native window failed.");
        return;
    }

    ANativeWindow_Buffer nwBuffer;
    // 锁住要绘图的表面
    if (ANativeWindow_lock(nativeWindow, &nwBuffer, nullptr) < 0) {
        LOGE("Lock native window failed.");
        return;
    }

    // 这里就直接给buffer设定颜色值了，代码没深究，颜色值肯定不对，只是测试能够设定颜色
    if (nwBuffer.format == WINDOW_FORMAT_RGB_565) {
        LOGD("Buffer format: RGB_565");
        memset(nwBuffer.bits, 200, sizeof(__uint16_t) * nwBuffer.height * nwBuffer.width);
    } else if (nwBuffer.format == WINDOW_FORMAT_RGBA_8888) {
        LOGD("Buffer format: RGBA_8888");
        memset(nwBuffer.bits, 200, sizeof(__uint32_t) * nwBuffer.height * nwBuffer.width);
    }

    // 释放先前的锁定，并把buffer的值post给显示器
    if (ANativeWindow_unlockAndPost(nativeWindow) < 0) {
        LOGE("Unlock window and post buffer failed.");
        return;
    }

    ANativeWindow_release(nativeWindow);
}
