/**
 *  author : sz
 *  date : 2023/7/19
 *  description : 
 */

#include <jni.h>
#include <GLES3/gl3.h>
#include <android/asset_manager_jni.h>
#include <thread>
#include "egl_util.h"
#include "shader/shader_yuv.h"


static const unsigned short width = 640;
static const unsigned short height = 272;

extern "C"
void loadYuv(JNIEnv *env, jobject thiz, jobject surface, jobject asset_manager) {

    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;
    GLuint program = useShader(V_SHADER_YUV, F_SHADER_YUV);
    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, -0.99f, 0.0f, 0.9f, 0.9f, // top right
            1.0f, 0.99f, 0.0f, 0.9f, 0.0f, // bottom right
            -1.0f, 0.99f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, -0.99f, 0.0f, 0.0f, 0.9f  // top left
    };
    // 顶点属性索引
    unsigned int indices[] = {
            0, 1, 3, // first triangle
            1, 2, 3  // second triangle
    };

    GLuint VBO, VAO, EBO;
    glGenVertexArrays(1, &VAO);
    // 绑定VAO
    glBindVertexArray(VAO);
    // 生成缓冲对象名字
    glGenBuffers(1, &VBO);
    // 绑定缓冲对象
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    // 为缓冲对象创建存储，大小为size，数据初始化为data指向的数据
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);
    // 指定顶点坐标的存放位置和格式
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 5 * sizeof(float), (void *) 0);
    // 启用顶点坐标数组，后面绘制的时候才能访问这些数据
    glEnableVertexAttribArray(0);
    // 指定纹理坐标的存放位置和格式
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 5 * sizeof(float),
                          (void *) (3 * sizeof(float)));
    // 启用纹理坐标数组
    glEnableVertexAttribArray(1);
    // 生成缓冲对象名字
    glGenBuffers(1, &EBO);
    // 绑定索引缓冲对象
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
    // 为索引缓冲对象创建存储，并利用data进行初始化
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indices), indices, GL_STATIC_DRAW);
    glBindVertexArray(0);

    GLuint textures[3];
    // 创建若干个纹理对象，并且得到纹理名字
    glGenTextures(3, textures);
    glBindTexture(GL_TEXTURE_2D, textures[0]);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);
    glBindTexture(GL_TEXTURE_2D, textures[1]);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);
    glBindTexture(GL_TEXTURE_2D, textures[2]);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width / 2, height / 2, 0, GL_LUMINANCE,
                 GL_UNSIGNED_BYTE, nullptr);

    // 设置着色器变量的值
    glUniform1i(glGetUniformLocation(program, "yTexture"), 0);
    glUniform1i(glGetUniformLocation(program, "uTexture"), 1);
    glUniform1i(glGetUniformLocation(program, "vTexture"), 2);

    // 顶点变换矩阵
    float matrix[16] = {
            0.5, 0.0, 0.0, 0.0, //第一列
            0.0, 0.5, 0.0, 0.0, //第二列
            0.0, 0.0, 1.0, 0.0, //第三列
            0.0, 0.0, 0.0, 1.0 //第四列
    };
    GLint m_location = glGetUniformLocation(program, "matrix");
    glUniformMatrix4fv(m_location, 1, false, matrix);

    // 通过Java层传入的AssetManager对象得到AAssetManager对象指针
    AAssetManager *aAssetManager = AAssetManager_fromJava(env, asset_manager);
    // 得到AAsset对象指针
    AAsset *aAsset = AAssetManager_open(aAssetManager, "video1_640_272.yuv",
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
        // u分量
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, textures[1]);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE, buf[1]);
        // v分量
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, textures[2]);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width / 2, height / 2, GL_LUMINANCE,
                        GL_UNSIGNED_BYTE, buf[2]);

        /*****绘制*****/
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, (void *) 0);
        eglSwapBuffers(eglConfigInfo.display, eglConfigInfo.eglSurface);
        glBindVertexArray(0);
        /*****绘制*****/
        // 线程休眠
        std::this_thread::sleep_for(std::chrono::milliseconds(30));
    }
    glDeleteProgram(program);
}








