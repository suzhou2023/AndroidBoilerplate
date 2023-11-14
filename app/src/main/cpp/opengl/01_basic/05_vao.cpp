/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include "shader_util.h"


// VAO
extern "C"
void vao(JNIEnv *env, jobject thiz, jobject surface) {
    //todo: 配置EGL
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

    //第一个三角形顶点属性数组
    static float triangleVerWithColor[] = {
            0.0f, 0.8f, 0.0f,//顶点
            1.0, 0.0, 0.0,//颜色
            0.8f, 0.8f, 0.0f,//顶点
            0.0, 1.0, 0.0,//颜色
            0.0f, 0.0f, 0.0f,//顶点
            0.0, 0.0, 1.0,//颜色
    };
    //第二个三角形顶点属性数组
    static float triangleVerWithColor1[] = {
            0.0f, -0.8f, 0.0f,//顶点
            1.0, 0.0, 0.0,//颜色
            -0.8f, -0.8f, 0.0f,//顶点
            0.0, 1.0, 0.0,//颜色
            0.0f, 0.0f, 0.0f,//顶点
            0.0, 0.0, 1.0,//颜色
    };

    unsigned int VBOs[2];
    unsigned int VAOs[2];
    //创建2个VAO
    glGenVertexArrays(2, VAOs); // we can also generate multiple VAOs or buffers at the same time
    glGenBuffers(2, VBOs);
    //绑定VAO[0]，从此在解绑VAO之前的所有对VBOs[0]的操作都会记录在VAO[0]内部
    glBindVertexArray(VAOs[0]);

    glBindBuffer(GL_ARRAY_BUFFER, VBOs[0]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(triangleVerWithColor), triangleVerWithColor,
                 GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, (void *) 0);
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, (void *) (3 * 4));
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    //解绑VAO
    glBindVertexArray(0);

    //绑定VAO[1]，从此在解绑VAO之前的所有对VBOs[1]的操作都会记录在VAO[1]内部
    glBindVertexArray(VAOs[1]);
    glBindBuffer(GL_ARRAY_BUFFER, VBOs[1]);
    glBufferData(GL_ARRAY_BUFFER, sizeof(triangleVerWithColor1), triangleVerWithColor1,
                 GL_STATIC_DRAW);

    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, (void *) 0);
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, (void *) (3 * 4));
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    //解绑VAO
    glBindVertexArray(0);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //绘制的时候再次绑定VAO[0]，表示后面的绘制数据取自VAO[0]缓存的VBO[0]的川村数据
    //原来绑定解绑VBO的代码可以去掉了，因为VBO的状态已经缓存在VAO了
    glBindVertexArray(VAOs[0]);
    glDrawArrays(GL_TRIANGLES, 0, 3);
    //todo:窗口显示，交换双缓冲区

    //解绑VAO[0]
    glBindVertexArray(0);
    glDeleteProgram(program);
}
