/**
 *  author : suzhou
 *  date : 2023/7/15 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include "EglUtil.h"
#include "LogUtil.h"


static char vShaderStr[] =
        "#version 300 es                          \n"
        "layout(location = 0) in vec4 vPosition;  \n"
        "void main()                              \n"
        "{                                        \n"
        "   gl_Position = vPosition;              \n"
        "}                                        \n";
static char fShaderStr[] =
        "#version 300 es                              \n"
        "precision mediump float;                     \n"
        "out vec4 fragColor;                          \n"
        "void main()                                  \n"
        "{                                            \n"
        "   fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );  \n"
        "}                                            \n";

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_egl_GLSurfaceViewNative_drawTriangleNative(
        JNIEnv *env,
        jobject thiz,
        jobject surface) {

    // 配置EGL
    EglConfigInfo *p_eglConfigInfo = configEGL(env, surface);
    if (p_eglConfigInfo == nullptr) return;

    // 加载着色器并创建渲染程序
    GLuint program = useShader(vShaderStr, fShaderStr);

    /*****将顶点数据传入图形渲染管线*****/
    static float triangleVer[] = {
            0.8f, -0.8f, 0.0f,
            -0.8f, -0.8f, 0.0f,
            0.0f, 0.8f, 0.0f,
    };
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, triangleVer);
    glEnableVertexAttribArray(0);

    /*****将图形渲染到屏幕*****/
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 3);
    eglSwapBuffers(p_eglConfigInfo->display, p_eglConfigInfo->eglSurface);

    glDeleteProgram(program);
}
