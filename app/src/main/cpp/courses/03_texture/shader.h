/**
 *  author : sz
 *  date : 2023/7/18
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_SHADER_H
#define ANDROIDBOILERPLATE_SHADER_H


static const char V_SHADER[] =
        "#version 300 es\n"
        "layout(location = 0)\n"
        "in vec4 vPosition;\n"

        "layout(location = 1)\n"
        "in vec2 texCoord;\n"

        "out vec2 fTexCoord;\n"

        "void main()\n"
        "{\n"
        "   gl_Position = vPosition;\n"
        "   fTexCoord = texCoord;\n"
        "   //fTexCoord = vec2(texCoord.x, 1.0 - texCoord.y);\n"
        "}\n";


static const char F_SHADER[] =
        "#version 300 es\n"
        "precision mediump float;\n"

        "in vec2 fTexCoord;\n"
        "out vec4 FragColor;\n"

        "uniform sampler2D texture1;\n"
        "uniform sampler2D texture2;\n"

        "void main()\n"
        "{\n"
        "    FragColor=mix(texture(texture1,fTexCoord),texture(texture2,fTexCoord),0.7);\n"
        "}\n";


#endif //ANDROIDBOILERPLATE_SHADER_H
