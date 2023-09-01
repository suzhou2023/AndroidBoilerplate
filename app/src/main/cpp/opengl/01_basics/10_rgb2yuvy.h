/**
 *  author : sz
 *  date : 2023/9/1
 *  description : rgb转yuvy
 */


#ifndef ANDROIDBOILERPLATE_10_RGB2YUVY_H
#define ANDROIDBOILERPLATE_10_RGB2YUVY_H

#include <jni.h>
#include "fboUtil.h"


extern "C"
void rgb2yuvy(JNIEnv *env, jobject thiz, GLContext *glContext, jobject bitmap, jobject callback) {

    eglUtil.makeCurrent(glContext, glContext->eglSurface[0]);

    GLuint texture2d;
    glUtil.genTex2D(&texture2d);

    uint32_t width, height;
    glUtil.texImage2D(env, bitmap, &width, &height);

    GLuint fbo, tex_2d;
    fboUtil_createFbo(width / 2, height, &fbo, &tex_2d);

    LOGD("1=================rgb2yuvy,%dx%d", width, height);

    // 一次偏移采样的偏移值
    float offset = 1.0 / (width / 2) * 0.5;
    glUniform1f(glGetUniformLocation(glContext->program[0], "offset"), offset);

    LOGD("2=================rgb2yuvy");


    glBindFramebuffer(GL_FRAMEBUFFER, fbo);


    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, tex_2d);

    glUtil.drawElements(6);

    LOGD("3=================rgb2yuvy");

    void *pixels = malloc(width / 2 * height * 4);

    glReadPixels(0, 0, width / 2, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);


    LOGD("4=================rgb2yuvy");

    jclass clazz = env->GetObjectClass(callback);
    jmethodID method = env->GetMethodID(clazz, "callback", "(Ljava/nio/ByteBuffer;II)V");
    jobject buffer = env->NewDirectByteBuffer(pixels, width / 2 * height * 4);
    env->CallVoidMethod(callback, method, buffer, width / 2, height);


    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    LOGD("5=================rgb2yuvy");

    free(pixels);
}


#endif //ANDROIDBOILERPLATE_10_RGB2YUVY_H





















