/**
 *  author : suzhou
 *  date : 2023/11/15 
 *  description : 
 */




#ifndef ANDROIDBOILERPLATE_10_RGB2VYUY_H
#define ANDROIDBOILERPLATE_10_RGB2VYUY_H


#include <jni.h>
#include "GLContext.h"

void rgb2vyuy(JNIEnv *env, jobject thiz, GLContext *glContext, jobject bitmap, jobject callback);

#endif //ANDROIDBOILERPLATE_10_RGB2VYUY_H
