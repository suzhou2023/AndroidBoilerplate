/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>
#include "shader.h"
#include "GLContext.h"
#include "egl_util.h"


extern "C"
JNIEXPORT jint JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_SurfaceViewGL_nativeSurfaceCreated(
        JNIEnv *env, jobject thiz, jobject surface) {
    // config EGL
    EGLConfigInfo *p_EglConfigInfo = static_cast<EGLConfigInfo *>(malloc(sizeof(EGLConfigInfo)));
    if (configEGL(env, surface, p_EglConfigInfo) < 0) return -1;
    GLContext::getInstance().setEglConfigInfo(p_EglConfigInfo);

    // 创建并使用着色器程序
    GLuint program = useShader(V_SHADER, F_SHADER);
    GLContext::getInstance().setProgram(program);

    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
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
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    // 生成缓冲对象
    glGenBuffers(1, &EBO);
    // 绑定索引缓冲对象
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    // 为索引缓冲对象创建存储，并利用data进行初始化
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);

    GLuint texture;
    // 生成纹理对象
    glGenTextures(1, &texture);
    // 绑定纹理对象到oes纹理目标
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture);
    // 对着色器中的纹理单元变量进行赋值
    glUniform1i(glGetUniformLocation(program, "oesTexture"), 0);
    // 激活纹理单元
    glActiveTexture(GL_TEXTURE0);
    return texture;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_SurfaceViewGL_nativeSetMatrix(
        JNIEnv *env, jobject thiz, jfloatArray matrix) {
    auto *p = (jfloat *) env->GetFloatArrayElements(matrix, nullptr);
    float array[] = {
            p[0], p[1], p[2], p[3],
            p[4], p[5], p[6], p[7],
            p[8], p[9], p[10], p[11],
            p[12], p[13], p[14], p[15]
    };
    // 获取matrix在shader中的位置引用
    GLuint program = GLContext::getInstance().getProgram();
    GLint matrixLocation = glGetUniformLocation(program, "matrix");
    // 修改对应的shader变量matrix
    glUniformMatrix4fv(matrixLocation, 1, GL_FALSE, array);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_SurfaceViewGL_nativeDrawFrame(
        JNIEnv *env, jobject thiz) {
    EGLConfigInfo *p_EglConfigInfo = GLContext::getInstance().getEglConfigInfo();
    /*****绘制*****/
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
    eglSwapBuffers(p_EglConfigInfo->display, p_EglConfigInfo->eglSurface);
    /*****绘制*****/
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_SurfaceViewGL_nativeSurfaceDestroyed(
        JNIEnv *env, jobject thiz) {
    // 删除着色器程序
    GLuint program = GLContext::getInstance().getProgram();
    glDeleteProgram(program);
    GLContext::getInstance().setProgram(0);
    // 销毁EGL context，释放资源
    EGLConfigInfo *p_EglConfigInfo = GLContext::getInstance().getEglConfigInfo();
    if (p_EglConfigInfo != nullptr) {
        destroyEGL(p_EglConfigInfo);
        free(p_EglConfigInfo);
        GLContext::getInstance().setEglConfigInfo(nullptr);
    }
}


extern "C"
JNIEXPORT jlong JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_SurfaceViewGL_nativeEglCreateContext(
        JNIEnv *env, jobject thiz, jobject surface) {
    EGLConfigInfo *p_EglConfigInfo = createContext(env, surface);
    if (p_EglConfigInfo == nullptr) return reinterpret_cast<jlong>(nullptr);
    return reinterpret_cast<jlong>(p_EglConfigInfo);
}



extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_encode_H264Encoder_nativeCreateSharedContext(
        JNIEnv *env, jobject thiz,jlong egl_config_info) {

    createContext(env, )
    eglCreatePbufferSurface()
}