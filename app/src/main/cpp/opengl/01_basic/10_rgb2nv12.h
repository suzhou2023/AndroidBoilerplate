/**
 *  author : suzhou
 *  date : 2023/11/14 
 *  description : 
 */




#ifndef ANDROIDBOILERPLATE_10_RGB2NV12_H
#define ANDROIDBOILERPLATE_10_RGB2NV12_H


#include <jni.h>
#include "GLContext.h"

void rgb2nv12(JNIEnv *env, jobject thiz, GLContext *glContext, jobject bitmap, jobject callback);

#endif //ANDROIDBOILERPLATE_10_RGB2NV12_H
