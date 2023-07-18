/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <atomic>
#include "EglUtil.h"
#include "LogUtil.h"
#include "shader.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1native_1egl_1triangle_SurfaceViewTest_drawTriangleWithNativeEGL(
        JNIEnv *env,
        jobject thiz,
        jobject surface) {

    // 配置EGL
    if (configEGL(env, surface) < 0) return;
    // program
    GLuint program = useShader(vShaderStr, fShaderStr);

    float vertices[] = {
            0.0f, 0.8f, 0.0f,//顶点
            1.0, 0.0, 0.0,//颜色

            0.8f, 0.8f, 0.0f,//顶点
            0.0, 1.0, 0.0,//颜色

            0.0f, 0.0f, 0.0f,//顶点
            0.0, 0.0, 1.0,//颜色

            -0.8f, 0.2f, 0.0f,//顶点
            0.0, 0.0, 1.0,//颜色
    };
    unsigned int indices[] = {
            0, 1, 2, // 第一个三角形
            1, 2, 3  // 第二个三角形
    };

    GLuint VAO, VBO, EBO;
    glGenVertexArrays(1, &VAO); // we can also generate multiple VAO or buffers at the same time
    glGenBuffers(1, &VBO);
    glGenBuffers(1, &EBO);

    //依次绑定VAO,VBO,EBO,顺序不能错
    glBindVertexArray(VAO);
    //VBO
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    //EBO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, (void *) 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, (void *) (3 * 4));
    glEnableVertexAttribArray(1);

    //解绑顺序和绑定要相反
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);

    //清屏
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //绘制三角形
    glBindVertexArray(VAO);
    glDrawArrays(GL_TRIANGLES, 0, 3);
    // todo: 通过顶点索引方式绘制无效？
//    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    glBindVertexArray(0);
    //窗口显示，交换双缓冲区
    eglSwapBuffers(g_EglConfigInfo.display, g_EglConfigInfo.eglSurface);
    glDeleteProgram(program);
}
