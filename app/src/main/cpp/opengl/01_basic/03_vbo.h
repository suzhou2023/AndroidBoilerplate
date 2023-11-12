/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include "GLContext.h"



// 顶点缓冲对象
extern "C"
void vbo(JNIEnv *env, jobject thiz, GLContext *glContext);