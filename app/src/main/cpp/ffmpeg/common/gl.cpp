/**
 *  author : sz
 *  date : 2023/7/19
 *  description : 
 */

#include "gl.h"

#include <GLES3/gl3.h>
#include "gl/gl_util.h"


// gl绘制yuv
void gl_drawYuv(GLContext *glContext, int frame_w, int frame_h, void *data_y, void *data_u, void *data_v) {

    // y平面
    if (glContext->texture[0] == 0) {
        gl_genTex2D(&glContext->texture[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, frame_w, frame_h, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE, nullptr);
    }
    // u平面
    if (glContext->texture[1] == 0) {
        gl_genTex2D(&glContext->texture[1]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, frame_w / 2, frame_h / 2, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE, nullptr);
    }
    // v平面
    if (glContext->texture[2] == 0) {
        gl_genTex2D(&glContext->texture[2]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, frame_w / 2, frame_h / 2, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE, nullptr);
    }

    // 设置着色器变量的值
    glUniform1i(glGetUniformLocation(glContext->program[0], "y_texture"), 0);
    glUniform1i(glGetUniformLocation(glContext->program[0], "u_texture"), 1);
    glUniform1i(glGetUniformLocation(glContext->program[0], "v_texture"), 2);


    // 激活纹理单元 y分量
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, glContext->texture[0]);
    // 替换纹理，比重新使用glTexImage2D性能高多
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, frame_w, frame_h, GL_LUMINANCE, GL_UNSIGNED_BYTE,
                    data_y);

    // 激活纹理单元 u分量
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, glContext->texture[1]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, frame_w / 2, frame_h / 2, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, data_u);

    // 激活纹理单元 v分量
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, glContext->texture[2]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, frame_w / 2, frame_h / 2, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, data_v);

    // 绘制
    gl_drawElements(6);
    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);
}















































