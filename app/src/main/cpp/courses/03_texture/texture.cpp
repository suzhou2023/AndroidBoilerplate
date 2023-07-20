/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/bitmap.h>
#include "EglUtil.h"
#include "shader.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__103_1texture_SurfaceViewTest_drawTexture(
        JNIEnv *env, jobject thiz, jobject surface, jobject bitmap1, jobject bitmap2) {

    // 获取bitmap的信息和数据指针
    AndroidBitmapInfo bmpInfo1, bmpInfo2;
    if (AndroidBitmap_getInfo(env, bitmap1, &bmpInfo1) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }
    if (AndroidBitmap_getInfo(env, bitmap2, &bmpInfo2) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }
    void *bmpPixels1;
    AndroidBitmap_lockPixels(env, bitmap1, &bmpPixels1);
    void *bmpPixels2;
    AndroidBitmap_lockPixels(env, bitmap2, &bmpPixels2);

    // EGL配置
    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;
    // 创建并使用着色器程序
    GLuint program = useShader(V_SHADER, F_SHADER);
    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, -0.5f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, 0.5f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, 0.5f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, -0.5f, 0.0f, 0.0f, 1.0f  // top left
    };
    // 顶点属性索引
    unsigned int indices[] = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };

    GLuint VBO, VAO, EBO;
    glGenVertexArrays(1, &VAO);
    // 绑定VAO
    glBindVertexArray(VAO);
    // 生成缓冲对象名字
    glGenBuffers(1, &VBO);
    // 绑定缓冲对象
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    // 为缓冲对象创建存储，大小为size，数据初始化为data指向的数据
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    // 指定顶点坐标的存放位置和格式
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float),
                          (void *) 0);
    // 启用顶点坐标数组，后面绘制的时候才能访问这些数据
    glEnableVertexAttribArray(0);
    // 指定纹理坐标的存放位置和格式
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float),
                          (void *) (3 * sizeof(float)));
    // 启用纹理坐标数组
    glEnableVertexAttribArray(1);
    // 生成缓冲对象名字
    glGenBuffers(1, &EBO);
    // 绑定索引缓冲对象
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    // 为索引缓冲对象创建存储，并利用data进行初始化
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices,
                 GL_STATIC_DRAW);

    GLuint texture1, texture2;
    // 生成纹理名字（已经创建纹理对象？）
    glGenTextures(1, &texture1);
    // 绑定一个纹理对象到将要使用的纹理类型
    glBindTexture(GL_TEXTURE_2D, texture1);
    // 横坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    // 纵坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // 纹理分辨率大于图元分辨率，即纹理需要被缩小的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    // 纹理分辨率小于图元分辨率，即纹理需要被放大的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    // 指定纹理图片
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo1.width, bmpInfo1.height, 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, bmpPixels1);
    AndroidBitmap_unlockPixels(env, bitmap1);
    // 下面的一样
    glGenTextures(1, &texture2);
    glBindTexture(GL_TEXTURE_2D, texture2);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo2.width, bmpInfo2.height, 0, GL_RGBA,
                 GL_UNSIGNED_BYTE, bmpPixels2);
    AndroidBitmap_unlockPixels(env, bitmap2);

    // 对着色器中的纹理单元变量进行赋值，这里分别赋值为0和1
    glUniform1i(glGetUniformLocation(program, "texture1"), 0);
    glUniform1i(glGetUniformLocation(program, "texture2"), 1);
    // 激活纹理单元，下面的绑定就会将对应的纹理对象和激活的纹理单元关联上，不得不说有点绕
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture1);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texture2);

    /*****绘制*****/
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glBindVertexArray(VAO);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
    glBindVertexArray(0);
    /*****绘制*****/

    // 释放着色器程序对象
    glDeleteProgram(program);
}