/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/bitmap.h>
#include "egl_util.h"
#include "gl_util.h"
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

    // EGL配置
    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;
    // 创建并使用着色器程序
    GLuint program = useShader(V_SHADER_TEX, F_SHADER_TEX);
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
    genBuffer(&VBO, vertices, sizeof(vertices));
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
    genIndexBuffer(&EBO, indices, sizeof(indices));

    GLuint texture;
    // 创建2d纹理对象，绑定和配置
    genTex2D(&texture);
    // 指定纹理图片
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo.width, bmpInfo.height,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, bmpPixels);
    AndroidBitmap_unlockPixels(env, bitmap);

    // todo: 对着色器中的纹理单元变量进行赋值(图层概念？)
    glUniform1i(glGetUniformLocation(program, "tex"), 10);
    // 激活纹理单元，下面的绑定就会将对应的纹理对象和激活的纹理单元关联上，不得不说有点绕
    glActiveTexture(GL_TEXTURE10);
    glBindTexture(GL_TEXTURE_2D, texture);
    // 绘制
    draw(eglConfigInfo, 3);
    // 释放着色器程序对象
    glDeleteProgram(program);
}