/**
 *  author : suzhou
 *  date : 2023/7/30 
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_GL_UTIL_H
#define ANDROIDBOILERPLATE_GL_UTIL_H

#include <GLES3/gl3.h>
#include <android/bitmap.h>


/**
 * 创建顶点缓冲对象并填充数据
 * @param vbo
 * @param data
 * @param size
 */
static void genBuffer(GLuint *vbo, float data[], GLuint size) {
    // 创建缓冲对象
    glGenBuffers(1, vbo);
    // 绑定缓冲对象
    glBindBuffer(GL_ARRAY_BUFFER, *vbo);
    // 为缓冲对象创建存储，大小为size，数据初始化为data指向的数据
    glBufferData(GL_ARRAY_BUFFER, size, data, GL_STATIC_DRAW);
}


/**
 * 创建顶点索引缓冲对象并填充数据
 * @param ebo
 * @param data
 * @param size
 */
static void genIndexBuffer(GLuint *ebo, GLuint data[], GLuint size) {
    // 创建缓冲对象
    glGenBuffers(1, ebo);
    // 绑定索引缓冲对象
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, *ebo);
    // 为索引缓冲对象创建存储，并利用data进行初始化
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, size, data, GL_STATIC_DRAW);
}


/**
 * 创建2d纹理对象，绑定和配置
 * @param tex_2d
 */
static void genTex2D(GLuint *tex_2d) {
    // 创建纹理对象
    glGenTextures(1, tex_2d);
    // 绑定一个纹理对象
    glBindTexture(GL_TEXTURE_2D, *tex_2d);
    // 横坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    // 纵坐标环绕配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    // 纹理分辨率大于图元分辨率，即纹理需要被缩小的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    // 纹理分辨率小于图元分辨率，即纹理需要被放大的过滤配置
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
}


/**
 * 加载bitmap到当前绑定的2D纹理
 * @param env
 * @param bitmap
 */
static void texImage2D(JNIEnv *env, jobject bitmap) {
    // 获取bitmap的信息和数据的指针
    AndroidBitmapInfo bitmapInfo;
    if (AndroidBitmap_getInfo(env, bitmap, &bitmapInfo) < 0) {
        LOGE("Get bitmap info failed.");
        return;
    }
    void *bitmapPixels;
    AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels);
    // todo: 加载图片到纹理（通过这个函数把图片数据加载到显卡了？）
    glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            bitmapInfo.width,
            bitmapInfo.height,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            bitmapPixels
    );
    AndroidBitmap_unlockPixels(env, bitmap);
}


/**
 * draw
 * @param glContext
 * @param vertexNum
 */
static void glDraw(GLuint vertexNum) {
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    glDrawElements(GL_TRIANGLES, vertexNum, GL_UNSIGNED_INT, (void *) 0);
}


#endif //ANDROIDBOILERPLATE_GL_UTIL_H
