/**
 *  author : suzhou
 *  date : 2023/11/13 
 *  description : 
 */




#ifndef ANDROIDBOILERPLATE_FBO_UTIL_H
#define ANDROIDBOILERPLATE_FBO_UTIL_H

#include <GLES3/gl3.h>


void fbo_createFbo(uint32_t width, uint32_t height, GLuint *fbo, GLuint *tex_2d);


#endif //ANDROIDBOILERPLATE_FBO_UTIL_H
