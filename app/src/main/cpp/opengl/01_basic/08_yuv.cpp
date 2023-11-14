/**
 *  author : sz
 *  date : 2023/7/19
 *  description : 
 */

#include "08_yuv.h"

#include <jni.h>
#include <GLES3/gl3.h>
#include <cstring>
#include "GLContext.h"
#include "gl_util.h"
#include "asset_util.h"


void loadYuv(JNIEnv *env, jobject thiz, GLContext *glContext) {

    GLuint textures[3];

    gl_genTex2D(&textures[0]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, 840, 1074, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    gl_genTex2D(&textures[1]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, 840 / 2, 1074 / 2, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    gl_genTex2D(&textures[2]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, 840 / 2, 1074 / 2, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    // 设置着色器变量的值
    glUniform1i(glGetUniformLocation(glContext->program[0], "yTexture"), 0);
    glUniform1i(glGetUniformLocation(glContext->program[0], "uTexture"), 1);
    glUniform1i(glGetUniformLocation(glContext->program[0], "vTexture"), 2);

    // 读取yuv图片到数组中
    GLubyte *buf = asset_readFile(glContext->assetManager, "YUV_Image_840x1074");
    // 注意：数组过大一定要动态申请，定义局部变量会报段错误
    GLubyte *buf_y = new GLubyte[840 * 1074];
    GLubyte *buf_u = new GLubyte[840 * 1074 / 4];
    GLubyte *buf_v = new GLubyte[840 * 1074 / 4];

    // 拷贝y分量
    memcpy(buf_y, buf, 840 * 1074);

    // 拷贝u分量
    // 注意：这里i的初始值一开始没从0开始，一直报地址错误，卡了我好久
    for (int i = 0; i < 840 * 1074 / 4; i += 1) {
        buf_u[i] = buf[840 * 1074 + i];
    }

    // 拷贝v分量
    for (int i = 0; i < 840 * 1074 / 4; i += 1) {
        buf_v[i] = buf[840 * 1074 + 840 * 1074 / 4 + i];
    }

    // 释放buf指向的数组
    delete[] buf;


    // 激活纹理单元 y分量
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, textures[0]);
    // 替换纹理，比重新使用glTexImage2D性能高多
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 840, 1074, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, buf_y);

    // 激活纹理单元 u分量
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, textures[1]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 840 / 2, 1074 / 2, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, buf_u);

    // 激活纹理单元 v分量
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, textures[2]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 840 / 2, 1074 / 2, GL_LUMINANCE,
                    GL_UNSIGNED_BYTE, buf_v);

    // 绘制
    gl_drawElements(6);
    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);

    delete[] buf_y;
    delete[] buf_u;
    delete[] buf_v;
}








