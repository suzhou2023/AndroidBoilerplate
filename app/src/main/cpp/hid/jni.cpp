#include <jni.h>
#include "hid.h"

/**
 *  author : sz
 *  date : 2024/1/2
 *  description : 
 */



extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_usb_HidDeviceUtil_hidRead(JNIEnv *env, jobject thiz, jint vendor_id, jint product_id, jint file_descriptor) {
    hidRead(nullptr, file_descriptor);
}