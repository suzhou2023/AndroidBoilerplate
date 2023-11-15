/**
 *  author : suzhou
 *  date : 2023/11/13 
 *  description : 
 */




#ifndef ANDROIDBOILERPLATE_GL_UTIL_H
#define ANDROIDBOILERPLATE_GL_UTIL_H

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/bitmap.h>


/**
     * 创建顶点缓冲对象并填充数据
     * @param vbo
     * @param data
     * @param size
     */
void gl_genBuffer(GLuint *vbo, float data[], GLuint size);

/**
 * 创建顶点索引缓冲对象并填充数据
 * @param ebo
 * @param data
 * @param size
 */
void gl_genIndexBuffer(GLuint *ebo, GLuint data[], GLuint size);

/**
 * 创建2d纹理对象，绑定和配置
 * @param tex_2d
 */
void gl_genTex2D(GLuint *tex_2d);

/**
 * 加载bitmap到当前绑定的2D纹理
 * @param env
 * @param bitmap
 */
void gl_texImage2D(JNIEnv *env, jobject bitmap, uint32_t *width, uint32_t *height);

/**
 * 绘制
 * @param vertexNum
 */
void gl_drawElements(GLuint vertexNum);


#endif //ANDROIDBOILERPLATE_GL_UTIL_H































