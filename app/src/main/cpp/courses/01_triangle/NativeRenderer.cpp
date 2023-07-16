//
// Created by sz on 2023/7/13.
//

#include <GLES3/gl3.h>
#include "NativeRenderer.h"
#include "01_triangle.h"


NativeRenderer::NativeRenderer() {
    m_pArt = new Triangle();
}

NativeRenderer::~NativeRenderer() {
    if (m_pArt) {
        delete m_pArt;
        m_pArt = nullptr;
    }
}

void NativeRenderer::onSurfaceCreated() {
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
}

void NativeRenderer::onSurfaceChanged(int width, int height) {
    glViewport(0, 0, width, height);
    m_ScreenW = width;
    m_ScreenH = height;
}

void NativeRenderer::onDrawFrame() {
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    if (m_pArt) {
        m_pArt->init();
        m_pArt->draw(m_ScreenW, m_ScreenH);
    }
}


