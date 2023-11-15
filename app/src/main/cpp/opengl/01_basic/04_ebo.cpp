/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#include "04_ebo.h"

#include <GLES3/gl3.h>
#include "gl/shader_util.h"



// 顶点索引缓冲对象
extern "C"
void ebo(JNIEnv *env, jobject thiz, jobject surface) {
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
    GLuint program = shader_createProgram(V_SHADER, F_SHADER);
    glUseProgram(program);

    float vertices[] = {
            0.5f, 0.5f, 0.0f,   // 右上角
            1.0, 0.0, 0.0,//右上角颜色

            0.5f, -0.5f, 0.0f,  // 右下角
            0.0, 0.0, 1.0,//右下角颜色

            -0.5f, -0.5f, 0.0f, // 左下角
            0.0, 1.0, 0.0,//左下角颜色

            -0.5f, 0.5f, 0.0f,   // 左上角
            0.5, 0.5, 0.5,//左上角颜色
    };

    unsigned int indices[] = {
            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
    };

    unsigned int EBO;
    //创建EBO缓冲对象
    glGenBuffers(1, &EBO);
    //绑定EBO缓冲对象
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    //给EBO缓冲对象传入索引数据
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
    //解析顶点属性数据。
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, vertices);
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, vertices + 3);
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //通过顶点索引绘制图元，注意这里已经绑定了EBO，所以最后一个参数传入的内存是数据再EBO中内存的起始地址偏移量
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    //todo:窗口显示，交换双缓冲区

    //解绑EBO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glDeleteBuffers(1, &EBO);
    glDeleteProgram(program);
}

