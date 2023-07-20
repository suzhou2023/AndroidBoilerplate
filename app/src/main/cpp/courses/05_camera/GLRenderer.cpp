/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>
#include "LogUtil.h"
#include "EglUtil.h"
#include "shader.h"
#include "GLRenderer.h"

static GLRenderer s_glRenderer = GLRenderer::getInstance();

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__105_1camera_SurfaceViewTest_nativeSurfaceCreated(
        JNIEnv *env, jobject thiz, jobject surface) {

    EglConfigInfo *p_EglConfigInfo = s_glRenderer.getEglConfigInfo();
    if (configEGL(env, surface, p_EglConfigInfo) < 0) return;
    // 创建并使用着色器程序
    GLuint program = useShader(V_SHADER, F_SHADER);
    s_glRenderer.setProgram(program);

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

    GLuint VBO, EBO;
    // 生成缓冲对象名字
    glGenBuffers(1, &VBO);
    // 绑定缓冲对象
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    // 为缓冲对象创建存储，大小为size，数据初始化为data指向的数据
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    // 指定顶点坐标的存放位置和格式
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) 0);
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
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_bbt2000_boilerplate_demos_gles__105_1camera_SurfaceViewTest_nativeCreateTexture(
        JNIEnv *env, jobject thiz, jobject surface) {
    GLuint texture;
    glGenTextures(1, &texture);

    // 绑定一个纹理对象到将要使用的纹理类型
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture);
    s_glRenderer.setOESTextureId(texture);
    return texture;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__105_1camera_SurfaceViewTest_nativeDrawFrame(
        JNIEnv *env, jobject thiz) {

    EglConfigInfo *p_EglConfigInfo = s_glRenderer.getEglConfigInfo();
    GLuint program = s_glRenderer.getProgram();
    GLuint oesTextureId = s_glRenderer.getOESTextureId();

    // 对着色器中的纹理单元变量进行赋值，这里分别赋值为0和1
    glUniform1i(glGetUniformLocation(program, "oesTexture"), 0);
    // 激活纹理单元，下面的绑定就会将对应的纹理对象和激活的纹理单元关联上，不得不说有点绕
    glActiveTexture(GL_TEXTURE0);
//    glBindTexture(GL_TEXTURE_2D, oesTextureId);
    /*****绘制*****/
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    eglSwapBuffers(p_EglConfigInfo->display, p_EglConfigInfo->eglSurface);
    /*****绘制*****/
}







