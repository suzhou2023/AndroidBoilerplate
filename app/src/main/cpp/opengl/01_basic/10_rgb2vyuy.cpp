/**
 *  author : sz
 *  date : 2023/9/1
 *  description : rgb转yuv
 */


#include <jni.h>
#include "GLContext.h"
#include "fbo_util.h"
#include "gl_util.h"


extern "C"
void rgb2vyuy(JNIEnv *env, jobject thiz, GLContext *glContext, jobject bitmap, jobject callback) {

    // 创建一个2d纹理并绑定，用来采样
    GLuint texture2d;
    genTex2D(&texture2d);

    // 给纹理设置图像数据
    uint32_t width, height;
    texImage2D(env, bitmap, &width, &height);

    // 创建fbo和关联的2d纹理，作为渲染的目的地
    GLuint fbo, tex_2d;
    fboUtil_createFbo(width / 2, height, &fbo, &tex_2d);
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

    glUniform1f(glGetUniformLocation(glContext->program[0], "offset"), offset);
    glUniform1i(glGetUniformLocation(glContext->program[0], "texture2d"), 0);

    glViewport(0, 0, width / 2, height);
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);

    // glReadPixels
    void *pixels = malloc(width * height * 2);
    glReadPixels(0, 0, width / 2, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

    // 数据回调java层
    jclass clazz = env->GetObjectClass(callback);
    jmethodID method = env->GetMethodID(clazz, "callback", "(Ljava/nio/ByteBuffer;II)V");
    // todo: 这里貌似有一次数据拷贝，并不是直接使用的pixels的内存
    jobject buffer = env->NewDirectByteBuffer(pixels, width * height * 2);
    env->CallVoidMethod(callback, method, buffer, width / 2, height);

    // 内存一定要释放
    free(pixels);
}























