/**
 *  author : suzhou
 *  date : 2023/9/3 
 *  description : 
 */

#include <jni.h>
#include <android/asset_manager_jni.h>
#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>
#include "AssetUtil.h"
#include "EglUtil.h"
#include "GLContext.h"
#include "GlUtil.h"


jlong GLContext::create(JNIEnv *env, jlong other_glcontext, jobject asset_manager) {

    EGLContext shareContext = EGL_NO_CONTEXT;
    if (other_glcontext > 0) {
        auto *otherGLContext = reinterpret_cast<GLContext *>(other_glcontext);
        if (otherGLContext->eglContext != nullptr) {
            shareContext = otherGLContext->eglContext;
        }
    }

    auto *glContext = new GLContext();
    EGLBoolean ret = eglUtil.createContext(glContext, shareContext);
    if (ret <= 0) {
        delete glContext;
        return reinterpret_cast<jlong>(nullptr);
    }

    // 保存assetManager
    glContext->assetManager = AAssetManager_fromJava(env, asset_manager);
    LOGI("GLContext::create success.");

    return reinterpret_cast<jlong>(glContext);
}


jboolean GLContext::createEglSurface(JNIEnv *env, jlong gl_context, jobject surface, jint index) {

    if (gl_context <= 0) return EGL_FALSE;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    EGLBoolean ret = eglUtil.eglCreateSurface(env, glContext, surface, index);
    if (ret == EGL_TRUE) {
        eglUtil.makeCurrent(glContext, glContext->eglSurface[index]);
        return EGL_TRUE;
    }

    return EGL_FALSE;
}

void GLContext::createProgram(JNIEnv *env, jobject thiz, jlong gl_context, jstring vName, jstring fName, jint index) {

    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    // jstring转c字符串
    const char *v_name = env->GetStringUTFChars(vName, nullptr);
    const char *f_name = env->GetStringUTFChars(fName, nullptr);

    GLubyte *buf_v = assetUtil.readFile(glContext->assetManager, v_name);
    GLubyte *buf_f = assetUtil.readFile(glContext->assetManager, f_name);

    // 释放字符串
    env->ReleaseStringUTFChars(vName, v_name);
    env->ReleaseStringUTFChars(fName, f_name);

    GLuint program = shaderUtil.createProgram(reinterpret_cast<const char *>(buf_v),
                                              reinterpret_cast<const char *>(buf_f));

    // 释放字符数组
    delete buf_v;
    delete buf_f;

    if (program <= 0) return;
    glContext->program[index] = program;
    glUseProgram(program);

    LOGI("GLContext::createProgram success.");
}


void GLContext::loadVertices(jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

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
    glUtil.genBuffer(&vbo, vertices, sizeof(vertices));
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) 0);
    glEnableVertexAttribArray(0);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) (3 * 4));
    glEnableVertexAttribArray(1);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glUtil.genIndexBuffer(&ebo, indices, sizeof(indices));
    glContext->vbo[0] = vbo;
    glContext->ebo[0] = ebo;

    LOGI("GLContext::loadVertices success.");
}


void GLContext::surfaceChanged(jlong gl_context, jint format, jint width, jint height) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    glContext->format = format;
    glContext->width = width;
    glContext->height = height;

    // todo: 不太好
    glContext->frame_data = static_cast<GLubyte *>(malloc(width * height * 4));
}


jint GLContext::createOesTexture(jlong gl_context) {
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

    glContext->oesTexture = texture;
    LOGI("GLContext::createOesTexture success.");

    return texture;
}


void GLContext::createFbo(jlong gl_context, jint width, jint height) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    GLuint fbo, texture;
    glUtil.genTex2D(&texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
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

    glContext->fbo[0] = fbo;
    glContext->fboTexture = texture;

    LOGI("GLContext::createFbo success.");
}


void GLContext::setMatrix(JNIEnv *env, jlong gl_context, jfloatArray matrix) {
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

    LOGI("GLContext::setMatrix success.");
}


void GLContext::destroy(jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    delete glContext;
}

















