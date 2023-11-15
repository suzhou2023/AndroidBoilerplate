/**
 *  author : sz
 *  date : 2023/7/19
 *  description : 
 */

#include "gl.h"

#include <GLES3/gl3.h>
#include "gl/gl_util.h"


void gl_drawYuv(GLContext *glContext, int width, int height, void *data_y, void *data_u, void *data_v) {

    // y平面
    if (glContext->texture[0] == 0) {
        gl_genTex2D(&glContext->texture[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE, nullptr);
    }
    // u平面
    if (glContext->texture[1] == 0) {
        gl_genTex2D(&glContext->texture[1]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE, nullptr);
    }
    // v平面
    if (glContext->texture[2] == 0) {
        gl_genTex2D(&glContext->texture[2]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE, nullptr);
    }

    // 设置着色器变量的值
    glUniform1i(glGetUniformLocation(glContext->program[0], "yTexture"), 0);
    glUniform1i(glGetUniformLocation(glContext->program[0], "uTexture"), 1);
    glUniform1i(glGetUniformLocation(glContext->program[0], "vTexture"), 2);

    // 顶点变换矩阵
    float matrix[16] = {
            1.0, 0.0, 0.0, 0.0, //第一列
            0.0, 1.0, 0.0, 0.0, //第二列
            0.0, 0.0, 1.0, 0.0, //第三列
            0.0, 0.0, 0.0, 1.0 //第四列
    };
    GLint m_location = glGetUniformLocation(glContext->program[0], "matrix");
    glUniformMatrix4fv(m_location, 1, false, matrix);

    // 激活纹理单元 y分量
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, glContext->texture[0]);
    // 替换纹理，比重新使用glTexImage2D性能高多
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_LUMINANCE, GL_UNSIGNED_BYTE,
                    data_y);

    // 激活纹理单元 u分量
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, glContext->texture[1]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, data_u);

    // 激活纹理单元 v分量
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, glContext->texture[2]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, data_v);

    // 绘制
    gl_drawElements(6);
    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);
}















































