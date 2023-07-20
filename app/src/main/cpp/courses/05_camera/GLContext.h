/**
 *  author : sz
 *  date : 2023/7/20
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_GLRENDERER_H
#define ANDROIDBOILERPLATE_GLRENDERER_H

#include <GLES3/gl3.h>
#include "EglUtil.h"


class GLContext {
public:
    static GLContext &getInstance() {
        static GLContext instance;
        return instance;
    }

    GLContext() {};

    ~GLContext() {};

    EglConfigInfo *getEglConfigInfo() {
        return p_EglConfigInfo;
    }

    void setEglConfigInfo(EglConfigInfo *p) {
        p_EglConfigInfo = p;
    }

    GLuint getProgram() {
        return mProgram;
    }

    void setProgram(GLuint program) {
        mProgram = program;
    }

    GLuint getOESTextureId() const {
        return mOESTextureId;
    }

    void setOESTextureId(GLuint textureId) {
        mOESTextureId = textureId;
    }

    int getWidth() const {
        return mWidth;
    }

    int getHeight() const {
        return mHeight;
    }

    void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

private:
    EglConfigInfo *p_EglConfigInfo{nullptr};
    GLuint mProgram{0};
    GLuint mOESTextureId{0};
    int mWidth{0};
    int mHeight{0};
};


#endif //ANDROIDBOILERPLATE_GLRENDERER_H
