/**
 *  author : sz
 *  date : 2023/7/20
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_GLCONTEXT_H
#define ANDROIDBOILERPLATE_GLCONTEXT_H

#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include "log_util.h"


class GLContext {
public:

    GLContext() {};

    ~GLContext() {
        if (fboTexture > 0) {
            glDeleteTextures(1, &fboTexture);
            LOGI("GL delete texture: %d", fboTexture);
            fboTexture = 0;
        }
        if (oesTexture > 0) {
            glDeleteTextures(1, &oesTexture);
            LOGI("GL delete texture: %d", oesTexture);
            oesTexture = 0;
        }
        for (int i = 0; i < sizeof(texture) / sizeof(texture[0]); i++) {
            if (texture[i] > 0) {
                glDeleteTextures(1, &texture[i]);
                LOGI("GL delete texture: %d", texture[i]);
                oesTexture = 0;
            }
        }
        for (int i = 0; i < sizeof(fbo) / sizeof(fbo[0]); i++) {
            if (fbo[i] > 0) {
                glDeleteFramebuffers(1, &fbo[i]);
                LOGI("GL delete frame buffer: %d", fbo[i]);
                fbo[i] = 0;
            }
        }
        for (int i = 0; i < sizeof(ebo) / sizeof(ebo[0]); i++) {
            if (ebo[i] > 0) {
                glDeleteBuffers(1, &ebo[i]);
                LOGI("GL delete buffer: %d", ebo[i]);
                ebo[i] = 0;
            }
        }
        for (int i = 0; i < sizeof(vbo) / sizeof(vbo[0]); i++) {
            if (vbo[i] > 0) {
                glDeleteBuffers(1, &vbo[i]);
                LOGI("GL delete buffer: %d", vbo[i]);
                vbo[i] = 0;
            }
        }
        for (int i = 0; i < sizeof(program) / sizeof(program[0]); i++) {
            if (program[i] > 0) {
                glDeleteProgram(program[i]);
                LOGI("GL delete program: %d", program[i]);
                program[i] = 0;
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
    GLuint program[5]{};
    GLuint vbo[5]{0};
    GLuint ebo[5]{0};
    GLuint fbo[5]{0};
    GLuint oesTexture{0};
    GLuint fboTexture{0};
    GLuint texture[5]{};
};


#endif //ANDROIDBOILERPLATE_GLCONTEXT_H
