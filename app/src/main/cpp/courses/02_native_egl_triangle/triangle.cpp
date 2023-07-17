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
        "#version 300 es\n"
        "layout(location = 0)\n"
        "in vec4 vPosition;\n"
        "void main()\n"
        "{\n"
        "   gl_Position = vPosition;\n"
        "   gl_PointSize = 50.0;\n"
        "}\n";

static char fShaderStr[] =
        "#version 300 es\n"
        "precision mediump float;\n"
        "out vec4 fragColor;\n"
        "void main()\n"
        "{\n"
        "   fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );\n"
        "}\n";


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1native_1egl_1triangle_SurfaceViewTest_drawTriangleWithNativeEGL(
        JNIEnv *env,
        jobject thiz,
        jobject surface) {

    // 配置EGL
    EglConfigInfo *p_eglConfigInfo = configEGL(env, surface);
    if (p_eglConfigInfo == nullptr) return;

    useShader(vShaderStr, fShaderStr);

    static float pointsVer[] = {
            0.8f, 0.8f, 0.0f,
            0.0f, 0.8f, 0.0f,
            0.4f, 0.4f, 0.0f,
            -0.8f, 0.5f, 0.0f,
            -0.4f, 0.8f, 0.0f,
            -0.8f, 0.8f, 0.0f,
    };

    //通过layout传输数据，传给了着色器中layout为0的变量
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, pointsVer);
    //打开layout为0的变量传输开关
    glEnableVertexAttribArray(0);

    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);


    glLineWidth(10);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 6);
    //窗口显示，交换后备缓冲区到前台
    eglSwapBuffers(p_eglConfigInfo->display, p_eglConfigInfo->eglSurface);
}
