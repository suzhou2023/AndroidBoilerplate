/**
 *  author : sz
 *  date : 2023/9/1
 *  description : rgb转yuv
 */


#ifndef ANDROIDBOILERPLATE_10_RGB2YUV_H
#define ANDROIDBOILERPLATE_10_RGB2YUV_H

#include <jni.h>
#include "fboUtil.h"


extern "C"
void rgb2yuv(JNIEnv *env, jobject thiz, GLContext *glContext, jobject bitmap, jobject callback) {

    // 创建一个2d纹理并绑定，用来采样
    GLuint texture2d;
    glUtil.genTex2D(&texture2d);

    // 给纹理设置图像数据
    uint32_t width, height;
    glUtil.texImage2D(env, bitmap, &width, &height);

    // 创建fbo和关联的2d纹理，作为渲染的目的地
    GLuint fbo, tex_2d;
    fboUtil_createFbo(width / 4, height * 1.5, &fbo, &tex_2d);
    // 用glContext管理起来，会统一做资源释放
    glContext->fbo[0] = fbo;
    glContext->fboTexture = tex_2d;

    // 启用fbo
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    // 绑定采样纹理
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture2d);

    // 着色器采样偏移值
    float offset = 1.0 / width;

    // Y采样
    glUseProgram(glContext->program[0]);
    glUniform1f(glGetUniformLocation(glContext->program[0], "offset"), offset);
    glUniform1i(glGetUniformLocation(glContext->program[0], "texture2d"), 0);

    glViewport(0, 0, width / 4, height);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);

    // UV采样
    glUseProgram(glContext->program[1]);
    glUniform1f(glGetUniformLocation(glContext->program[1], "offset"), offset);
    glUniform1i(glGetUniformLocation(glContext->program[1], "texture2d"), 0);

    glViewport(0, height, width / 4, height * 0.5);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);

    // glReadPixels
    void *pixels = malloc(width * height * 1.5);
    glReadPixels(0, 0, width / 4, height * 1.5, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

    // todo: 不需要swap?
//    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);

    // 数据回调java层
    jclass clazz = env->GetObjectClass(callback);
    jmethodID method = env->GetMethodID(clazz, "callback", "(Ljava/nio/ByteBuffer;II)V");
    // todo: 这里貌似有一次数据拷贝，并不是直接使用的pixels的内存
    jobject buffer = env->NewDirectByteBuffer(pixels, width * height * 1.5);
    env->CallVoidMethod(callback, method, buffer, width / 4, height * 1.5);

    // 内存一定要释放
    free(pixels);
}


#endif //ANDROIDBOILERPLATE_10_RGB2YUV_H





















