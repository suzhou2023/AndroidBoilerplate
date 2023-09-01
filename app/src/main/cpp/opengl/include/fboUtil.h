/**
 *  author : sz
 *  date : 2023/9/1
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_FBOUTIL_H
#define ANDROIDBOILERPLATE_FBOUTIL_H


#include "GlUtil.h"


static void fboUtil_createFbo(uint32_t width, uint32_t height, GLuint *fbo, GLuint *tex_2d) {

    glUtil.genTex2D(tex_2d);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);

    glGenFramebuffers(1, fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, *fbo);
    glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            *tex_2d,
            0
    );

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        LOGE("glCheckFramebufferStatus error.");
        return;
    }

    glBindFramebuffer(GL_FRAMEBUFFER, 0);
}


#endif //ANDROIDBOILERPLATE_FBOUTIL_H





















