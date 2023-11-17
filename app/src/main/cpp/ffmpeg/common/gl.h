/**
 *  author : sz
 *  date : 2023/11/15
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_GL_H
#define ANDROIDBOILERPLATE_GL_H


#include "gl/GLContext.h"


/**
 * opengl绘制yuv
 *
 * @param glContext
 * @param frame_w - 帧width
 * @param frame_h - 帧height
 * @param data_y
 * @param data_u
 * @param data_v
 */
void gl_drawYuv(GLContext *glContext, int frame_w, int frame_h, void *data_y, void *data_u, void *data_v);

#endif //ANDROIDBOILERPLATE_GL_H


































