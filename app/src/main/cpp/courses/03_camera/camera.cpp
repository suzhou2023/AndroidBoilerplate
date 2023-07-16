/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include <cstring>
#include "LogUtil.h"
#include "EglUtil.h"

/*****传入surface进行直接绘制的例子，传入颜色涂满整个surface*****/
extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_draw_1color_GLSurfaceViewNative_drawColorNative(
        JNIEnv *env, jobject obj,
        jobject surface, jint color) {
    configEGL(env, surface);

    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    if (nativeWindow == nullptr) {
        LOGE("Acquire native window failed.");
        return;
    }

    ANativeWindow_Buffer nwBuffer;
    if (ANativeWindow_lock(nativeWindow, &nwBuffer, nullptr) < 0) {
        LOGE("Lock native window failed.");
        return;
    }

    if (nwBuffer.format == WINDOW_FORMAT_RGB_565) {
        LOGD("Buffer format: RGB_565");
        memset(nwBuffer.bits, color, sizeof(__uint16_t) * nwBuffer.height * nwBuffer.width);
    } else if (nwBuffer.format == WINDOW_FORMAT_RGBA_8888) {
        LOGD("Buffer format: RGBA_8888");
        memset(nwBuffer.bits, color, sizeof(__uint32_t) * nwBuffer.height * nwBuffer.width);
    }

    if (ANativeWindow_unlockAndPost(nativeWindow) < 0) {
        LOGE("Unlock window and post buffer failed.");
        return;
    }

    ANativeWindow_release(nativeWindow);
}
