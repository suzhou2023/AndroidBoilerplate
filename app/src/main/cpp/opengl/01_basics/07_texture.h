/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/bitmap.h>
#include "shader/shader_tex.h"


extern "C"
void texture(JNIEnv *env, jobject thiz, jobject surface, jobject bitmap) {
    // 获取bitmap的信息和数据指针
    AndroidBitmapInfo bmpInfo;
    if (AndroidBitmap_getInfo(env, bitmap, &bmpInfo) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }
    void *bmpPixels;
    AndroidBitmap_lockPixels(env, bitmap, &bmpPixels);

    // todo: EGL配置

    // 创建并使用着色器程序
    GLuint program = shaderUtil.createProgram(V_SHADER_TEX, F_SHADER_TEX);
    glUseProgram(program);
    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, 0.5f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, -0.5f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, -0.5f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, 0.5f, 0.0f, 0.0f, 1.0f,  // top left
            0.0f, 0.5f, 0.0f, 0.5f, 1.0f, // top middle
    };
    // 顶点属性索引
    unsigned int indices[] = {
            1, 2, 4, // f
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };

    GLuint VBO, EBO;
    // 创建顶点缓冲并填充数据
    glUtil.genBuffer(&VBO, vertices, sizeof(vertices));
    // 指定顶点坐标的存放位置和格式
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE,
                          5 * sizeof(float), (void *) 0);
    // 启用顶点坐标数组，后面绘制的时候才能访问这些数据
    glEnableVertexAttribArray(0);
    // 指定纹理坐标的存放位置和格式
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE,
                          5 * sizeof(float), (void *) (3 * sizeof(float)));
    // 启用纹理坐标数组
    glEnableVertexAttribArray(1);
    // 创建顶点索引缓冲并填充数据
    glUtil.genIndexBuffer(&EBO, indices, sizeof(indices));

    GLuint texture;
    // 创建2d纹理对象，绑定和配置
    glUtil.genTex2D(&texture);
    // 指定纹理图片
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo.width, bmpInfo.height,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, bmpPixels);
    AndroidBitmap_unlockPixels(env, bitmap);

    glUniform1i(glGetUniformLocation(program, "layer"), 3);
    // 激活纹理单元(图层)，下面的绑定就会将对应的纹理对象和激活的纹理单元关联上，不得不说有点绕
    glActiveTexture(GL_TEXTURE3);
    glBindTexture(GL_TEXTURE_2D, texture);
    // 绘制
    glUtil.drawElements(3);
    // todo

    // 释放着色器程序对象
    glDeleteProgram(program);
}