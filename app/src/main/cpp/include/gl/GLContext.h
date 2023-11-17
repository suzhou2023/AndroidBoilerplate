/**
 *  author : sz
 *  date : 2023/7/20
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_GLCONTEXT_H
#define ANDROIDBOILERPLATE_GLCONTEXT_H

#include <jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <cstdlib>
#include <android/asset_manager.h>
#include "android_log.h"


class GLContext {
public:
    // 创建EGLSurface
    bool createEGLSurface(JNIEnv *env, jobject surface, jint index);

    // 创建着色器程序
    int createProgram(JNIEnv *env, jobject thiz, jstring v_name, jstring f_name, jint index);

    // 加载顶点坐标和纹理坐标
    void loadVertices();

    // 创建oes纹理
    int createOesTexture();

    // 创建fbo
    void createFbo(int width, int height, int index);

    // 设置矩阵
    void setMatrix(JNIEnv *env, jlong gl_context, jfloatArray matrix);

    // 矩阵配置：根据图形和窗口的尺寸配置合适的矩阵(顶点坐标变换矩阵、纹理坐标变换矩阵)
    void configMatrix(int program_index, int frame_w, int frame_h, int window_w, int window_h, int scale_type, bool rotate);

    GLContext() {};

    ~GLContext() {
        if (oesTexture > 0) {
            glDeleteTextures(1, &oesTexture);
            LOGI("GL delete oesTexture: %d", oesTexture);
        }
        for (int i = 0; i < sizeof(texture) / sizeof(texture[0]); i++) {
            if (texture[i] > 0) {
                glDeleteTextures(1, &texture[i]);
                LOGI("GL delete texture: %d", texture[i]);
            }
        }
        for (int i = 0; i < sizeof(fboTexture) / sizeof(fboTexture[0]); i++) {
            if (fboTexture[i] > 0) {
                glDeleteTextures(1, &fboTexture[i]);
                LOGI("GL delete fboTexture: %d", fboTexture[i]);
            }
        }
        for (int i = 0; i < sizeof(fbo) / sizeof(fbo[0]); i++) {
            if (fbo[i] > 0) {
                glDeleteFramebuffers(1, &fbo[i]);
                LOGI("GL delete frame buffer: %d", fbo[i]);
            }
        }
        for (int i = 0; i < sizeof(ebo) / sizeof(ebo[0]); i++) {
            if (ebo[i] > 0) {
                glDeleteBuffers(1, &ebo[i]);
                LOGI("GL delete buffer: %d", ebo[i]);
            }
        }
        for (int i = 0; i < sizeof(vbo) / sizeof(vbo[0]); i++) {
            if (vbo[i] > 0) {
                glDeleteBuffers(1, &vbo[i]);
                LOGI("GL delete buffer: %d", vbo[i]);
            }
        }
        for (int i = 0; i < sizeof(program) / sizeof(program[0]); i++) {
            if (program[i] > 0) {
                glDeleteProgram(program[i]);
                LOGI("GL delete program: %d", program[i]);
            }
        }
        if (eglDisplay != nullptr) {
            for (int i = 0; i < sizeof(eglSurface) / sizeof(eglSurface[0]); i++) {
                if (eglSurface[i] != nullptr) {
                    EGLBoolean ret = eglDestroySurface(eglDisplay, eglSurface[i]);
                    if (ret == EGL_TRUE) {
                        LOGI("EGL destroy surface success: %p", eglSurface[i]);
                    }
                }
            }
            if (eglContext != nullptr) {
                EGLBoolean ret = eglDestroyContext(eglDisplay, eglContext);
                if (ret == EGL_TRUE) {
                    LOGI("EGL destroy context success: %p", eglContext);
                }
            }
            EGLBoolean ret = eglTerminate(eglDisplay);
            if (ret == EGL_TRUE) {
                LOGI("EGL terminate success: %p", eglDisplay);
            }
        }
    };

    EGLDisplay eglDisplay{nullptr};
    EGLConfig eglConfig{nullptr};
    EGLContext eglContext{nullptr};
    EGLSurface eglSurface[5]{nullptr};
    AAssetManager *assetManager{nullptr}; // native层资源管理器
    GLsizei width{0}; // 帧宽度
    GLsizei height{0}; // 帧高度
    GLuint program[5]{};
    GLuint vbo[5]{0};
    GLuint ebo[5]{0};
    GLuint fbo[5]{0};
    GLuint fboTexture[5]{0};
    GLuint texture[5]{};
    GLuint oesTexture{0};
};


#endif //ANDROIDBOILERPLATE_GLCONTEXT_H
