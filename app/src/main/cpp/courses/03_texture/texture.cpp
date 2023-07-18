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


// todo: two textures mix
extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__103_1texture_SurfaceViewTest_drawTexture(JNIEnv *env,
                                                                                  jobject thiz,
                                                                                  jobject surface,
                                                                                  jobject bitmap) {

    if (configEGL(env, surface) < 0) return;

    GLuint program = useShader(V_SHADER, F_SHADER);

    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            0.5f, 0.5f, 0.0f, 1.0f, 1.0f, // top right
            0.5f, -0.5f, 0.0f, 1.0f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, // bottom left
            -0.5f, 0.5f, 0.0f, 0.0f, 1.0f  // top left
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


    AndroidBitmapInfo bmpInfo;
    if (AndroidBitmap_getInfo(env, bitmap, &bmpInfo) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }

    void *bmpPixels;
    AndroidBitmap_lockPixels(env, bitmap, &bmpPixels);

    //纹理id
    unsigned int texture1;
    //创建纹理
    glGenTextures(1, &texture1);
    //绑定纹理
    glBindTexture(GL_TEXTURE_2D, texture1);

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


    //顶点坐标
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) 0);
    glEnableVertexAttribArray(0);
    //纹理坐标
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float),
                          (void *) (3 * sizeof(float)));
    glEnableVertexAttribArray(1);

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