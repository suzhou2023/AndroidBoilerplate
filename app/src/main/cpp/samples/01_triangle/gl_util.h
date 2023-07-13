//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_GL_UTIL_H
#define ANDROIDBOILERPLATE_GL_UTIL_H

#include <GLES2/gl2.h>


class GlUtil {
public:
    static GLuint loadShader(GLenum shaderType, const char *pSource);

    static GLuint createProgram(const char *pVertexShaderSource, const char *pFragShaderSource,
                                GLuint &vertexShaderHandle,
                                GLuint &fragShaderHandle);
};

#endif //ANDROIDBOILERPLATE_GL_UTIL_H
