/**
 *  author : suzhou
 *  date : 2023/7/16 
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_SHADERUTIL_H
#define ANDROIDBOILERPLATE_SHADERUTIL_H

#include <GLES3/gl3.h>
#include "LogUtil.h"


GLuint loadShader(const char *source, GLuint type) {
    // 创建shader
    GLuint shader = glCreateShader(type);
    if (shader == 0) {
        LOGE("Create shader failed, type = %d", type);
        return -1;
    }
    // 加载shader
    // 代码长度传0则读到字符串结尾
    glShaderSource(shader, 1, &source, 0);
    // 编译shader
    glCompileShader(shader);
    // 打印编译错误信息
    GLint status;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &status);
    if (status <= 0) {
        LOGE("Shader compile failed, type = %d", type);
        LOGE("Error shader source:\n%s", source);
        auto *infoLog = new GLchar[2048];
        GLsizei length;
        glGetShaderInfoLog(shader, 2048, &length, infoLog);
        LOGE("Shader compile info log:\n%s", infoLog);
        return -1;
    }
    LOGI("Shader compile success, type = %d", type);
    return shader;
}

GLuint useShader(const char *source_vShader, const char *source_fShader) {
    GLuint vShader = loadShader(source_vShader, GL_VERTEX_SHADER);
    GLuint fShader = loadShader(source_fShader, GL_FRAGMENT_SHADER);
    // 创建渲染程序
    GLuint program = glCreateProgram();
    if (program <= 0) {
        LOGE("GL create program failed.");
        return -1;
    }
    // 向渲染程序中加入着色器
    glAttachShader(program, vShader);
    glAttachShader(program, fShader);
    // 链接程序
    glLinkProgram(program);
    GLint status = -1;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status <= 0) {
        LOGE("GL link program failed.");
        return -1;
    }
    LOGI("GL link program success.");
    glDeleteShader(vShader);
    glDeleteShader(fShader);
    // 激活渲染程序
    glUseProgram(program);
    return program;
}


#endif //ANDROIDBOILERPLATE_SHADERUTIL_H
