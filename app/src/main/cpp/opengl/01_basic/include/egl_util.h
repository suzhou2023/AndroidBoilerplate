/**
 *  author : suzhou
 *  date : 2023/11/12 
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_EGL_UTIL_H
#define ANDROIDBOILERPLATE_EGL_UTIL_H

#include <EGL/egl.h>
#include "GLContext.h"

EGLBoolean createContext(GLContext *glContext, EGLContext shareContext);


#endif //ANDROIDBOILERPLATE_EGL_UTIL_H
