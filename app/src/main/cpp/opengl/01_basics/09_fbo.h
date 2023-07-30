/**
 *  author : suzhou
 *  date : 2023/7/30 
 *  description : 
 */

#include "gl_util.h"


void fbo(JNIEnv *env, jobject surface, jobject bitmap) {

    EglConfigInfo eglConfigInfo;
    if (configEGL(env, surface, &eglConfigInfo) < 0) return;
    GLuint program = useShader(V_SHADER_TEX, F_SHADER_TEX);

    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, 0.5f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, -0.5f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, -0.5f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, 0.5f, 0.0f, 0.0f, 1.0f,  // top left
            0.0f, 0.5f, 0.0f, 0.5f, 1.0f, // top middle
    };
    GLuint indices[] = {
            0, 1, 2,
            0, 2, 3,
            1, 2, 4
    };

    GLuint vbo, ebo;
    genBuffer(&vbo, vertices, sizeof(vertices));
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 4 * 5, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 4 * 5, (void *) (3 * 4));
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    genIndexBuffer(&ebo, indices, sizeof(indices));

    GLuint tex, tex2;
    genTex2D(&tex);
    texImage2D(env, bitmap);
    glBindTexture(GL_TEXTURE_2D, 0);
    genTex2D(&tex2);

    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1000, 1600,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
    glBindTexture(GL_TEXTURE_2D, 0);

    GLuint fbo;
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            tex2,
            0
    );

//    unsigned int rbo;
//    glGenRenderbuffers(1, &rbo);
//    glBindRenderbuffer(GL_RENDERBUFFER, rbo);
//    glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, 1800, 1600);
//    glBindRenderbuffer(GL_RENDERBUFFER, 0);
//
//    glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);

    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        LOGE("Frame buffer not complete.");
        return;
    }

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, tex);

    draw(eglConfigInfo, 6);

//    glBindTexture(GL_TEXTURE_2D, 0);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, tex2);

    draw(eglConfigInfo, 6);
}
