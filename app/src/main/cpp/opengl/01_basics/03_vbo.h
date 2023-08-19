/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <atomic>
#include "EglUtil.h"
#include "GlUtil.h"
#include "LogUtil.h"

// 顶点缓冲对象
extern "C"
void vbo(JNIEnv *env, jobject thiz, jobject surface) {
    // todo:配置EGL

    const char *V_SHADER =
            "#version 300 es\n"
            "layout (location = 0)\n"
            "in vec4 aPosition;\n"
            "layout (location = 1)\n"
            "in vec4 v_color;\n"
            "out vec4 v_color2;\n"
            "void main() {\n"
            "    gl_Position = aPosition;\n"
            "    v_color2 = v_color;\n"
            "}";

    const char *F_SHADER =
            "#version 300 es\n"
            "precision mediump float;\n"
            "in vec4 v_color2;\n"
            "out vec4 FragColor;\n"
            "void main() {\n"
            "    FragColor = v_color2;\n"
            "}";

    // program
    GLuint program = shaderUtil.createProgram(V_SHADER, F_SHADER);
    glUseProgram(program);

    static float triangleVerWithColor[] = {
            0.5f, 0.5f, 0.0f,//顶点
            1.0, 0.0, 0.0,//颜色

            -0.5f, 0.5f, 0.0f,//顶点
            0.0, 1.0, 0.0,//颜色

            0.0f, -0.5f, 0.0f,//顶点
            0.0, 0.0, 1.0,//颜色
    };

    GLuint VBOs[1];
    glGenBuffers(1, VBOs);
    glBindBuffer(GL_ARRAY_BUFFER, VBOs[0]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(triangleVerWithColor), triangleVerWithColor,
                 GL_STATIC_DRAW);
    //解析第一个VBO的顶点属性数据
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, (void *) 0);
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, (void *) (3 * 4));
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glDrawArrays(GL_TRIANGLES, 0, 4);
    //todo:窗口显示，交换双缓冲区

    //解绑EBO
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDeleteBuffers(1, VBOs);
    glDeleteProgram(program);
}
