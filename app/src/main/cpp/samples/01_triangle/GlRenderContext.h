//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_GLRENDERCONTEXT_H
#define ANDROIDBOILERPLATE_GLRENDERCONTEXT_H


//#include "stdint.h"
//#include <GLES3/gl3.h>
//#include "TextureMapSample.h"
//#include "NV21TextureMapSample.h"
//#include "Triangle.h"

#include "01_triangle.h"

class GlRenderContext {
    GlRenderContext();

    ~GlRenderContext();

public:
    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();

    static GlRenderContext *getInstance();

    static void destroyInstance();

private:
    static GlRenderContext *m_pContext;
    Triangle *m_pBeforeSample;
    Triangle *m_pCurSample;
    int m_ScreenW;
    int m_ScreenH;
};


#endif //ANDROIDBOILERPLATE_GLRENDERCONTEXT_H
