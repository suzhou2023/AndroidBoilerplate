/**
 *  author : sz
 *  date : 2023/11/21
 *  description : 
 */

#include <jni.h>
#include "01_tex_3d.h"
#include "02_render_cube.h"


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__103_13d_jni_Jni_tex3d(JNIEnv *env, jobject thiz, jlong gl_context,
                                                               jobject bitmap) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    tex_3d(env, glContext, 0, 1080, 1920, bitmap);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_bbt2000_boilerplate_demos_gles__103_13d_jni_Jni_renderCube(JNIEnv *env, jobject thiz, jlong gl_context) {
    if (gl_context <= 0) return;
    auto *glContext = reinterpret_cast<GLContext *>(gl_context);

    render_cube(glContext);
}






























