/**
 *  author : sz
 *  date : 2023/7/19
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/asset_manager_jni.h>
#include <thread>
#include "GLContext.h"
#include "gl_util.h"


extern "C"
void loadYuv2(JNIEnv *env, jobject thiz, GLContext *glContext) {
    const int width = 640;
    const int height = 272;

    GLuint textures[3];

    gl_genTex2D(&textures[0]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    gl_genTex2D(&textures[1]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    gl_genTex2D(&textures[2]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    // 设置着色器变量的值
    glUniform1i(glGetUniformLocation(glContext->program[0], "yTexture"), 0);
    glUniform1i(glGetUniformLocation(glContext->program[0], "uTexture"), 1);
    glUniform1i(glGetUniformLocation(glContext->program[0], "vTexture"), 2);

    // 顶点变换矩阵
    float matrix[16] = {
            1.0, 0.0, 0.0, 0.0, //第一列
            0.0, 1.0, 0.0, 0.0, //第二列
            0.0, 0.0, 1.0, 0.0, //第三列
            0.0, 0.0, 0.0, 1.0 //第四列
    };
    GLint m_location = glGetUniformLocation(glContext->program[0], "matrix");
    glUniformMatrix4fv(m_location, 1, false, matrix);

    // 得到AAsset对象指针
    // todo: 资源文件移到了assets目录外面，要运行本示例的话移回去
    AAsset *aAsset = AAssetManager_open(glContext->assetManager, "video1_640_272.yuv",
                                        AASSET_MODE_STREAMING);
    // 文件总长度
    off_t totalSize = AAsset_getLength(aAsset);
    // 总帧数
    uint64_t totalFrame = totalSize / (width * height * 3 / 2);
    // 创建3个buffer数组分别用于存放YUV三个分量
    unsigned char *buf[3];
    buf[0] = new unsigned char[width * height]; // y
    buf[1] = new unsigned char[width * height / 4]; // u
    buf[2] = new unsigned char[width * height / 4]; // v

    // 读取每帧的YUV数据
    for (int i = 0; i < totalFrame; ++i) {
        // 读取y分量
        int yBytesRead = AAsset_read(aAsset, buf[0], width * height);
        // 读取u分量
        int uBytesRead = AAsset_read(aAsset, buf[1], width * height / 4);
        // 读取v分量
        int vBytesRead = AAsset_read(aAsset, buf[2], width * height / 4);
        // 读到文件末尾或遇到错误
        if (yBytesRead <= 0 || uBytesRead <= 0 || vBytesRead <= 0) {
            AAsset_close(aAsset);
            break;
        }

        // 激活纹理单元 y分量
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        // 替换纹理，比重新使用glTexImage2D性能高多
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_LUMINANCE, GL_UNSIGNED_BYTE,
                        buf[0]);

        // 激活纹理单元 u分量
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, textures[1]);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE, buf[1]);

        // 激活纹理单元 v分量
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, textures[2]);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE, buf[2]);

        // 绘制
        gl_drawElements(6);
        eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);

        // 线程休眠
        std::this_thread::sleep_for(std::chrono::milliseconds(30));
    }

    delete[] buf[0];
    delete[] buf[1];
    delete[] buf[2];
}








