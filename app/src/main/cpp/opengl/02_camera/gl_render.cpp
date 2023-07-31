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
#include "gl_util.h"


extern "C"
JNIEXPORT jlong JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeCreateGLContext(
        JNIEnv *env, jobject thiz, jobject surface, jlong other_glcontext) {
    EGLContext shareContext = EGL_NO_CONTEXT;
    if (other_glcontext > 0) {
        auto *otherGLContext = reinterpret_cast<GLContext *>(other_glcontext);
        if (otherGLContext->eglContext != nullptr) {
            shareContext = otherGLContext->eglContext;
        }
    }

    auto *glContext = new GLContext();
    int ret = eglCreateContext(env, surface, glContext, shareContext);
    if (ret < 0) {
        delete glContext;
        return reinterpret_cast<jlong>(nullptr);
    }

    LOGD("Create EGL context success.");
    return reinterpret_cast<jlong>(glContext);
}

extern "C"
JNIEXPORT EGLBoolean JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeEglMakeCurrent(
        JNIEnv *env, jobject thiz, jlong gl_context) {
    if (gl_context <= 0) return EGL_FALSE;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    return eglMakeCurrent(glContext);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeConfigGL(
        JNIEnv *env, jobject thiz, jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    // 创建两个着色器程序
    GLuint program = createProgram(V_SHADER, F_SHADER_OES);
    if (program <= 0) return;
    glContext->program[0] = program;
    GLuint program2 = createProgram(V_SHADER2, F_SHADER_2D);
    if (program2 <= 0) return;
    glContext->program[1] = program2;

    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, 1.0f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, -1.0f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, -1.0f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
    };
    // 顶点属性索引
    GLuint indices[] = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };
    // vbo, ebo
    GLuint vbo, ebo;
    genBuffer(&vbo, vertices, sizeof(vertices));
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) (3 * 4));
    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    genIndexBuffer(&ebo, indices, sizeof(indices));
    glContext->vbo = vbo;
    glContext->ebo = ebo;
    LOGD("Config gl success.");
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeCreateOESTexture(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    if (gl_context <= 0) return -1;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    GLuint texture;
    glGenTextures(1, &texture);
    // todo: OES纹理似乎是永久绑定？
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, texture);
    // 着色器变量赋值
    glUseProgram(glContext->program[0]);
    // OES纹理单元(图层)赋值。
    // 不赋值的话，后面绘制的时候openGL会默认帮你激活图层0，我们再激活其它图层也是多余的。
    // 但是你一旦赋值，后面要激活的图层必须是这个值。
    // 配合FBO使用的时候，貌似只能设置为图层0。(目前测试结果，有待确认)
    glUniform1i(glGetUniformLocation(glContext->program[0], "oesTexture"), 0);
    glUseProgram(glContext->program[1]);
    // 2D纹理图层赋值。
    glUniform1i(glGetUniformLocation(glContext->program[1], "layer"), 1);
    LOGD("Create OES texture success.");

    glContext->oesTexture = texture;
    return texture;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_SurfaceViewGL_nativeCreateFbo(
        JNIEnv *env, jobject thiz, jlong gl_context, jint width, jint height) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    GLuint fbo, texture;
    genTex2D(&texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 800, 800, 0, GL_RGB, GL_UNSIGNED_BYTE, nullptr);
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            texture,
            0
    );
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        LOGE("Frame buffer not complete.");
        return;
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    glContext->fbo = fbo;
    glContext->fboTexture = texture;
    LOGD("Create FBO success.");
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeSetMatrix(
        JNIEnv *env, jobject thiz, jlong gl_context, jfloatArray matrix) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    auto *p = (jfloat *) env->GetFloatArrayElements(matrix, nullptr);
    float array[] = {
            p[0], p[1], p[2], p[3],
            p[4], p[5], p[6], p[7],
            p[8], p[9], p[10], p[11],
            p[12], p[13], p[14], p[15]
    };
    glUseProgram(glContext->program[0]);
    GLint matrixLocation = glGetUniformLocation(glContext->program[0], "matrix");
    glUniformMatrix4fv(matrixLocation, 1, GL_FALSE, array);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeDrawFrame(
        JNIEnv *env, jobject thiz, jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    if (glContext->fbo > 0) {
        glUseProgram(glContext->program[0]);
        glBindFramebuffer(GL_FRAMEBUFFER, glContext->fbo);
        glActiveTexture(GL_TEXTURE0);
        glDraw(6);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
    glUseProgram(glContext->program[1]);
    // 根据着色器变量赋值，激活对应图层。如果前面没赋值的话，这里可以随便激活一个图层，
    // 也可能是openGL默认帮我们激活了一个图层，我们这里激活的图层是无效的。有待确认。
    // 使用的时候尽量对应上吧，因为图层多了，还是需要一一对应的，没必要采用系统默认行为。
    glActiveTexture(GL_TEXTURE1);
    // 对于2D纹理，上面激活图层以后，还需要绑定一下2D纹理目标，图层才能和纹理对象关联，
    // 着色器的采样程序才能正常运行。
    glBindTexture(GL_TEXTURE_2D, glContext->fboTexture);
    glDraw(6);
    glBindTexture(GL_TEXTURE_2D, 0);
    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__102_1camera_jni_Jni_nativeDestroyGLContext(
        JNIEnv *env, jobject thiz, jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);
    delete glContext;
}





