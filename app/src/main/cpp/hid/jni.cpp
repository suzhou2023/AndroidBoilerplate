#include <jni.h>
#include "common/LibusbWrapper.h"
#include "hid.h"

/**
 *  author : sz
 *  date : 2024/1/2
 *  description : 
 */


extern "C"
JNIEXPORT jlong JNICALL
Java_com_bbt2000_boilerplate_demos_usb_HidDeviceUtil_libusbPrepare(JNIEnv *env, jobject thiz, jint file_descriptor) {
    auto *wrapper = new LibusbWrapper();

    int ret = wrapper->prepare(file_descriptor);
    if (ret != 0) {
        delete wrapper;
        return -1;
    }
    return reinterpret_cast<jlong>(wrapper);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_usb_HidDeviceUtil_libusbRelease(JNIEnv *env, jobject thiz, jlong libusb_wrapper) {
    auto *wrapper = reinterpret_cast<LibusbWrapper *>(libusb_wrapper);

    wrapper->release();
    delete wrapper;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_usb_HidDeviceUtil_libusbHidRead(JNIEnv *env, jobject thiz, jlong libusb_wrapper, jobject key_callback) {
    auto *wrapper = reinterpret_cast<LibusbWrapper *>(libusb_wrapper);

    int value = wrapper->hidRead();
    if (value > 0) {
        jclass clazz = env->GetObjectClass(key_callback);
        jmethodID method = env->GetMethodID(clazz, "onKey", "(I)V");
        env->CallVoidMethod(key_callback, method, value);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_usb_HidDeviceUtil_libusbHidReadAsync(JNIEnv *env, jobject thiz, jlong libusb_wrapper) {
    auto *wrapper = reinterpret_cast<LibusbWrapper *>(libusb_wrapper);

    wrapper->hidReadAsync();
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_usb_HidDeviceUtil_libusbHidRead2(JNIEnv *env, jobject thiz, jint file_descriptor) {
    hidRead(file_descriptor);
}
































