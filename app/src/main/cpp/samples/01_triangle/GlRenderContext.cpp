//
// Created by sz on 2023/7/13.
//

#include <GLES3/gl3.h>
#include "GlRenderContext.h"


GlRenderContext *GlRenderContext::m_pContext = nullptr;

GlRenderContext::GlRenderContext() {
    m_pBeforeSample = nullptr;
}

GlRenderContext::~GlRenderContext() {
    if (m_pCurSample) {
        delete m_pCurSample;
        m_pCurSample = nullptr;
    }

    if (m_pBeforeSample) {
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }

}


void GlRenderContext::onSurfaceCreated() {
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
}

void GlRenderContext::onSurfaceChanged(int width, int height) {
    glViewport(0, 0, width, height);
    m_ScreenW = width;
    m_ScreenH = height;
}

void GlRenderContext::onDrawFrame() {
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);

    if (m_pBeforeSample) {
        m_pBeforeSample->destroy();
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }

    if (m_pCurSample) {
        m_pCurSample->init();
        m_pCurSample->draw(m_ScreenW, m_ScreenH);
    }
}

GlRenderContext *GlRenderContext::getInstance() {
    if (m_pContext == nullptr) {
        m_pContext = new GlRenderContext();
    }
    return m_pContext;
}

void GlRenderContext::destroyInstance() {
    if (m_pContext) {
        delete m_pContext;
        m_pContext = nullptr;
    }
}


