/**
 *  author : sz
 *  date : 2023/7/14
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_BASEART_H
#define ANDROIDBOILERPLATE_BASEART_H

#include <GLES3/gl3.h>

class BaseArt {
public:
    BaseArt() {};

    virtual ~BaseArt() {};

    virtual void init() {};

    virtual void draw(int screenW, int screenH) {};

protected:
    GLuint m_VertexShader;
    GLuint m_FragmentShader;
    GLuint m_Program;
};


#endif //ANDROIDBOILERPLATE_BASEART_H
