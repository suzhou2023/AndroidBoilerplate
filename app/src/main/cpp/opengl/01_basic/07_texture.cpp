/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/bitmap.h>
#include "GLContext.h"
#include "android_log.h"
#include "gl_util.h"


extern "C"
void texture(JNIEnv *env, GLContext *glContext, jobject bitmap) {

    AndroidBitmapInfo bmpInfo;
    if (AndroidBitmap_getInfo(env, bitmap, &bmpInfo) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }

    void *bmpPixels;
    GLuint texture;
    // 创建2d纹理对象，绑定和配置
    gl_genTex2D(&texture);
    // 指定纹理图片
    AndroidBitmap_lockPixels(env, bitmap, &bmpPixels);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bmpInfo.width, bmpInfo.height,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, bmpPixels);
    AndroidBitmap_unlockPixels(env, bitmap);

    // 变量赋值
    glUniform1i(glGetUniformLocation(glContext->program[0], "tex2d"), 0);
    // 激活纹理单元(图层)，下面的绑定就会将对应的纹理对象和激活的纹理单元关联上，不得不说有点绕
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texture);

    // 绘制视口
    glViewport(200, 800, 600, 600);
    gl_drawElements(6);

    // 绘制视口
    glViewport(0, 0, 600, 600);
    // 不要清屏
    glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);

    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);
}














