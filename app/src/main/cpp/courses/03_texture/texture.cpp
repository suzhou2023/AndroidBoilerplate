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
        JNIEnv *env, jobject thiz, jobject surface, jobject bitmap, jobject bitmap2) {

    if (configEGL(env, surface) < 0) return;

    GLuint program = useShader(V_SHADER, F_SHADER);

    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, -0.5f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, 0.5f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, 0.5f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, -0.5f, 0.0f, 0.0f, 1.0f  // top left
    };

    unsigned int indices[] = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };

    GLuint VBO, VAO, EBO;
    glGenVertexArrays(1, &VAO);
    glGenBuffers(1, &VBO);
    glGenBuffers(1, &EBO);

    glBindVertexArray(VAO);
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);


    AndroidBitmapInfo bmpInfo, bmpInfo2;
    if (AndroidBitmap_getInfo(env, bitmap, &bmpInfo) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }
    if (AndroidBitmap_getInfo(env, bitmap2, &bmpInfo2) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }

    void *bmpPixels;
    AndroidBitmap_lockPixels(env, bitmap, &bmpPixels);
    void *bmpPixels2;
    AndroidBitmap_lockPixels(env, bitmap2, &bmpPixels2);


    //纹理id
    GLuint texture, texture2;
    //创建纹理
    glGenTextures(1, &texture);
    //绑定纹理
    glBindTexture(GL_TEXTURE_2D, texture);

    //纹理环绕配置
    //横坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    //纵坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    //纹理过滤配置
    //纹理分辨率大于图元分辨率，即纹理需要被缩小的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    //纹理分辨率小于图元分辨率，即纹理需要被放大的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo.width,
                 bmpInfo.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bmpPixels);
    AndroidBitmap_unlockPixels(env, bitmap);

    glGenTextures(1, &texture2);
    glBindTexture(GL_TEXTURE_2D, texture2);
    // set the texture wrapping parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
                    GL_REPEAT);  // set texture wrapping to GL_REPEAT (default wrapping method)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // set texture filtering parameters
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo2.width,
                 bmpInfo2.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, bmpPixels2);
    AndroidBitmap_unlockPixels(env, bitmap2);

    //顶点坐标
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE,
                          5 * sizeof(float), (void *) 0);
    glEnableVertexAttribArray(0);
    //纹理坐标
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE,
                          5 * sizeof(float), (void *) (3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    //对着色器中的纹理单元变量进行赋值
    glUniform1i(glGetUniformLocation(program, "ourTexture"), 0);
    glUniform1i(glGetUniformLocation(program, "ourTexture2"), 1);

    //将纹理单元和纹理对象进行绑定
    //激活纹理单元，下面的绑定就会和当前激活的纹理单元关联上
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture);
    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texture2);

    //清屏
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //绘制三角形
    glBindVertexArray(VAO);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    glBindVertexArray(0);
    //窗口显示，交换双缓冲区
    eglSwapBuffers(g_EglConfigInfo.display, g_EglConfigInfo.eglSurface);
    //释放着色器程序对象
    glDeleteProgram(program);
}