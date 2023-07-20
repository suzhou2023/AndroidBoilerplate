/**
 *  author : sz
 *  date : 2023/7/20
 *  description : 
 */

#ifndef ANDROIDBOILERPLATE_GLRENDERER_H
#define ANDROIDBOILERPLATE_GLRENDERER_H

#include <GLES3/gl3.h>
#include "EglUtil.h"


class GLRenderer {


public:
    static GLRenderer getInstance() {
        static GLRenderer instance;
        return instance;
    }

    GLRenderer() {};

    ~GLRenderer() {};


    EglConfigInfo *getEglConfigInfo() {
        return &mEglConfigInfo;
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

private:
    EglConfigInfo mEglConfigInfo;
    GLuint mProgram;
    GLuint mOESTextureId;
    int mWidth;
    int mHeight;
};


#endif //ANDROIDBOILERPLATE_GLRENDERER_H
