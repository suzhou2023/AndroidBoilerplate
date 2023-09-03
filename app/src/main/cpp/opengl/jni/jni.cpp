/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <GLES2/gl2ext.h>
#include <android/native_window_jni.h>
#include <cstring>
#include "LogUtil.h"
#include "AssetUtil.h"
#include "GLContext.h"
#include "EglUtil.h"
#include "GlUtil.h"


extern "C"
JNIEXPORT jlong JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeCreateGLContext(
        JNIEnv *env, jobject thiz, jlong other_glcontext, jobject asset_manager) {

    return GLContext::create(env, other_glcontext, asset_manager);
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeEGLCreateSurface(
        JNIEnv *env, jobject thiz, jlong gl_context, jobject surface, jint index) {

    return GLContext::createEglSurface(env, gl_context, surface, index);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeCreateProgram(
        JNIEnv *env, jobject thiz, jlong gl_context, jstring vName, jstring fName, jint index) {

    return GLContext::createProgram(env, thiz, gl_context, vName, fName, index);
}

/**
 * 加载顶点属性数组
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeLoadVertices(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    return GLContext::loadVertices(gl_context);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeSurfaceChanged(
        JNIEnv *env, jobject thiz, jlong gl_context, jint format, jint width, jint height) {

    return GLContext::surfaceChanged(gl_context, format, width, height);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeCreateOESTexture(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    return GLContext::createOesTexture(gl_context);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeCreateFbo(
        JNIEnv *env, jobject thiz, jlong gl_context, jint width, jint height) {

    return GLContext::createFbo(gl_context, width, height);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeSetMatrix(
        JNIEnv *env, jobject thiz, jlong gl_context, jfloatArray matrix) {

    return GLContext::setMatrix(env, gl_context, matrix);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeDrawFrame(
        JNIEnv *env, jobject thiz, jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    /*****FBO test*****/
//    if (glContext->fbo[0] > 0) {
//        glUseProgram(glContext->program[0]);
//        glBindFramebuffer(GL_FRAMEBUFFER, glContext->fbo[0]);
//        glActiveTexture(GL_TEXTURE0);
//        glDraw(6);
//        glBindFramebuffer(GL_FRAMEBUFFER, 0);
//    }

//    glUseProgram(glContext->program[1]);
    // 根据着色器变量赋值，激活对应图层。如果前面没赋值的话，这里可以随便激活一个图层，
    // 也可能是openGL默认帮我们激活了一个图层，我们这里激活的图层是无效的。有待确认。
    // 使用的时候尽量对应上吧，因为图层多了，还是需要一一对应的，没必要采用系统默认行为。
//    glActiveTexture(GL_TEXTURE1);
    // 对于2D纹理，上面激活图层以后，还需要绑定一下2D纹理目标，图层才能和纹理对象关联，
    // 着色器的采样程序才能正常运行。
//    glBindTexture(GL_TEXTURE_2D, glContext->fboTexture);
    // 画预览surface
//    if (glContext->eglSurface[0] != nullptr) {
//        eglMakeCurrent(glContext, glContext->eglSurface[0]);
//        glDraw(6);
//        eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);
//    }
    // 画录制surface
//    if (glContext->eglSurface[1] != nullptr) {
//        eglMakeCurrent(glContext, glContext->eglSurface[1]);
//        glDraw(6);
//        eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[1]);
//    }
//    glBindTexture(GL_TEXTURE_2D, 0);
    /*****FBO test*****/


    /*****ByteBuffer test*****/
//    static int mark = 0;
//    if (mark == 0) {
//        mark += 1;
//        int width = glContext->width;
//        int height = glContext->height;
//        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, glContext->frame_data);
//        unsigned char *p = static_cast<unsigned char *>(glContext->frame_data);
//        LOGD("%d-%d-%d-%d", *p, *(p + 1), *(p + 2), *(p + 3));
//        jclass clazz = env->GetObjectClass(callback);
//        jmethodID method = env->GetMethodID(clazz, "onFrame", "(Ljava/nio/ByteBuffer;)V");
//        jobject buffer = env->NewDirectByteBuffer(glContext->frame_data, width * height * 4);
//        env->CallVoidMethod(callback, method, buffer);
//    }
    /*****ByteBuffer test*****/


    // 不需要fbo，直接从oes纹理画到预览surface和codec input surface
    glUseProgram(glContext->program[0]);
    glActiveTexture(GL_TEXTURE0);
    if (glContext->eglSurface[0] != nullptr) {
        eglUtil.makeCurrent(glContext, glContext->eglSurface[0]);
        glUtil.drawElements(6);
        eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);
    }
    if (glContext->eglSurface[1] != nullptr) {
        eglUtil.makeCurrent(glContext, glContext->eglSurface[1]);
        glUtil.drawElements(6);
        eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[1]);
    }
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles_jni_Jni_nativeDestroyGLContext(
        JNIEnv *env, jobject thiz, jlong gl_context) {

    return GLContext::destroy(gl_context);
}















