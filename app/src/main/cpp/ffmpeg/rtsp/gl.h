/**
 *  author : sz
 *  date : 2023/11/15
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_GL_H
#define ANDROIDBOILERPLATE_GL_H


#include "gl/GLContext.h"

void gl_drawFrame(GLContext *glContext, int width, int height, void *data_y, void *data_u, void *data_v);

#endif //ANDROIDBOILERPLATE_GL_H
