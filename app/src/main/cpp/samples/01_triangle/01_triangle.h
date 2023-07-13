//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_01_TRIANGLE_H
#define ANDROIDBOILERPLATE_01_TRIANGLE_H


#include <GLES2/gl2.h>


class Triangle {
public:
    Triangle();

    virtual ~Triangle();

//    virtual void LoadImage(NativeImage *pImage);

    virtual void init();

    virtual void draw(int screenW, int screenH);

    virtual void destroy();

protected:
    GLuint m_VertexShader;
    GLuint m_FragmentShader;
    GLuint m_ProgramObj;
};

#endif //ANDROIDBOILERPLATE_01_TRIANGLE_H
