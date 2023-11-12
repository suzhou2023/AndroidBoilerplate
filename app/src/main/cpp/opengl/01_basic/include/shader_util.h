/**
 *  author : suzhou
 *  date : 2023/11/13 
 *  description : 
 */




#ifndef ANDROIDBOILERPLATE_SHADER_UTIL_H
#define ANDROIDBOILERPLATE_SHADER_UTIL_H


#include <GLES3/gl3.h>


GLuint loadShader(const char *source, GLuint type);

GLuint createProgram(const char *source_vShader, const char *source_fShader);


#endif //ANDROIDBOILERPLATE_SHADER_UTIL_H
