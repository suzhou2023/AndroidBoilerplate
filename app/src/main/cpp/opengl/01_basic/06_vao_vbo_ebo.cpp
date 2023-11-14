/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */
#include "06_vao_vbo_ebo.h"

#include <GLES3/gl3.h>
#include "shader/shader.h"
#include "shader_util.h"


// 结合使用
void vao_vbo_ebo(JNIEnv *env, jobject thiz, jobject surface) {
    //todo: 配置EGL

    // program
    GLuint program = shader_createProgram(V_SHADER_BASIC, F_SHADER_BASIC);
    glUseProgram(program);

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

    //依次绑定VAO,VBO,EBO
    glBindVertexArray(VAO);
    //VBO
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 24, (void *) 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 24, (void *) (3 * 4));
    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    //EBO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
    //VAO解绑时，要保持EBO的绑定状态，那样，在绘制阶段重新绑定VAO时，EBO才可用
    glBindVertexArray(0);

    //清屏
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //绘制三角形
    glBindVertexArray(VAO);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    // todo:

    //todo: 这里还需要解绑EBO吗？
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
    glDeleteProgram(program);
}
