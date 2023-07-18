/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_SHADER_H
#define ANDROIDBOILERPLATE_SHADER_H


static const char vShaderStr[] =
        "#version 300 es\n"
        "layout(location = 0)\n"
        "in vec4 vPosition;\n"
        "layout(location = 1)\n"
        "in vec4 vColor;\n"
        "out vec4 vColorOut;\n"
        "void main()\n"
        "{\n"
        "   gl_Position = vPosition;\n"
        "   vColorOut = vColor;\n"
        "}\n";


static const char fShaderStr[] =
        "#version 300 es\n"
        "precision mediump float;\n"
        "out vec4 fColor;\n"
        "in vec4 vColorOut;//输出的颜色\n"
        "void main()\n"
        "{\n"
        "   fColor = vColorOut;\n"
        "}\n";


#endif //ANDROIDBOILERPLATE_SHADER_H
