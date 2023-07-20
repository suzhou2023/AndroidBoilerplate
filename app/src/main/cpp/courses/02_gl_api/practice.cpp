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

// 顶点缓冲对象
extern "C"
void vbo(JNIEnv *env, jobject thiz, jobject surface) {
    // 配置EGL
    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;

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
    GLuint program = useShader(V_SHADER, F_SHADER);

    static float triangleVerWithColor[] = {
            0.0f, 0.8f, 0.0f,//顶点
            1.0, 0.0, 0.0,//颜色

            0.8f, 0.8f, 0.0f,//顶点
            0.0, 1.0, 0.0,//颜色

            0.0f, 0.0f, 0.0f,//顶点
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
    //窗口显示，交换双缓冲区
    eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
    //解绑EBO
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDeleteBuffers(1, VBOs);
    glDeleteProgram(program);
}

// 顶点索引缓冲对象
extern "C"
void ebo(JNIEnv *env, jobject thiz, jobject surface) {
    // 配置EGL
    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;

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
    GLuint program = useShader(V_SHADER, F_SHADER);

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
    //窗口显示，交换双缓冲区
    eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
    //解绑EBO
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glDeleteBuffers(1, &EBO);
    glDeleteProgram(program);
}

// VAO
extern "C"
void vao(JNIEnv *env, jobject thiz, jobject surface) {
    // 配置EGL
    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;

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
    GLuint program = useShader(V_SHADER, F_SHADER);

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
    //窗口显示，交换双缓冲区
    eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
    //解绑VAO[0]
    glBindVertexArray(0);
    glDeleteProgram(program);
}

// 结合使用
extern "C"
void vao_vbo_ebo(JNIEnv *env, jobject thiz, jobject surface) {
    // 配置EGL
    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;
    // program
    GLuint program = useShader(V_SHADER, F_SHADER);

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
    eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
    //todo: 这里还需要解绑EBO吗？
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
    glDeleteProgram(program);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1gl_1api_SurfaceViewTest_glApiPractice(
        JNIEnv *env,
        jobject thiz,
        jobject surface) {

    vao_vbo_ebo(env, thiz, surface);
}