/**
 *  author : suzhou
 *  date : 2023/11/12 
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_EGL_UTIL_H
#define ANDROIDBOILERPLATE_EGL_UTIL_H

#include <EGL/egl.h>
#include "GLContext.h"


EGLBoolean egl_createContext(GLContext *glContext, EGLContext shareContext);

EGLBoolean egl_createSurface(JNIEnv *env, GLContext *glContext, jobject surface, EGLint index);

EGLBoolean egl_makeCurrent(GLContext *glContext, EGLSurface eglSurface);


#endif //ANDROIDBOILERPLATE_EGL_UTIL_H
