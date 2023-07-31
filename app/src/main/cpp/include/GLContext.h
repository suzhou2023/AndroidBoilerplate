/**
 *  author : sz
 *  date : 2023/7/20
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_GLCONTEXT_H
#define ANDROIDBOILERPLATE_GLCONTEXT_H

#include <EGL/egl.h>
#include <GLES3/gl3.h>


class GLContext {
public:

    GLContext() {};

    ~GLContext() {
        if (oesTexture > 0) {
            glDeleteTextures(1, &oesTexture);
            oesTexture = 0;
        }
        if (fbo > 0) {
            glDeleteFramebuffers(1, &fbo);
            fbo = 0;
        }
        if (ebo > 0) {
            glDeleteBuffers(1, &ebo);
            ebo = 0;
        }
        if (vbo > 0) {
            glDeleteBuffers(1, &vbo);
            vbo = 0;
        }
        if (program[0] > 0) {
            glDeleteProgram(program[0]);
            program[0] = 0;
        }
        if (program[1] > 0) {
            glDeleteProgram(program[1]);
            program[1] = 0;
        }
        if (eglDisplay != nullptr) {
            if (eglDisplay != nullptr) {
                eglDestroySurface(eglDisplay, eglSurface);
            }
            if (eglContext != nullptr) {
                eglDestroyContext(eglDisplay, eglContext);
            }
            eglTerminate(eglDisplay);
        }
    };

    EGLDisplay eglDisplay{nullptr};
    EGLSurface eglSurface{nullptr};
    EGLContext eglContext{nullptr};
    GLuint program[2]{};
    GLuint vbo{0};
    GLuint ebo{0};
    GLuint fbo{0};
    GLuint fboTexture{0};
    GLuint oesTexture{0};
};


#endif //ANDROIDBOILERPLATE_GLCONTEXT_H
