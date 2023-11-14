/**
 *  author : suzhou
 *  date : 2023/7/30
 *  description :
 */

#include "09_fbo.h"

#include <GLES3/gl3.h>
#include "android_log.h"
#include "gl_util.h"


void fbo(JNIEnv *env, jobject surface, jobject bitmap) {
    // todo:

//    GLuint program = shaderUtil.createProgram(V_SHADER_TEX, F_SHADER_TEX);
//    GLuint program2 = shaderUtil.createProgram(V_SHADER_TEX, F_SHADER_TEX2);

    // 顶点坐标和纹理坐标
    float vertices[] = {
            // 前3个图元顶点坐标，后两个纹理坐标
            1.0f, 0.5f, 0.0f, 1.0f, 1.0f, // top right
            1.0f, -0.5f, 0.0f, 1.0f, 0.0f, // bottom right
            -1.0f, -0.5f, 0.0f, 0.0f, 0.0f, // bottom left
            -1.0f, 0.5f, 0.0f, 0.0f, 1.0f,  // top left
    };
    GLuint indices[] = {
            0, 1, 2,
            0, 2, 3,
    };

    GLuint vbo, ebo;
    gl_genBuffer(&vbo, vertices, sizeof(vertices));
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 4 * 5, 0);
    glVertexAttribPointer(1, 2, GL_FLOAT, GL_FALSE, 4 * 5, (void *) (3 * 4));
    glEnableVertexAttribArray(0);
    glEnableVertexAttribArray(1);
    gl_genIndexBuffer(&ebo, indices, sizeof(indices));

    GLuint tex;
    gl_genTex2D(&tex);
    // todo
//    glUtil.texImage2D(env, bitmap);
    glBindTexture(GL_TEXTURE_2D, 0);

    /*****fbo配置*****/
    GLuint fbo, tex2;
    gl_genTex2D(&tex2);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 1000, 1000,
                 0, GL_RGBA, GL_UNSIGNED_BYTE, nullptr);
    glBindTexture(GL_TEXTURE_2D, 0);
    glGenFramebuffers(1, &fbo);
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glFramebufferTexture2D(
            GL_FRAMEBUFFER,
            GL_COLOR_ATTACHMENT0,
            GL_TEXTURE_2D,
            tex2,
            0
    );
    if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        LOGE("Frame buffer not complete.");
        return;
    }
    glBindFramebuffer(GL_FRAMEBUFFER, 0);
    /*****fbo配置*****/

    /*****绘制阶段*****/
    //todo
//    glUseProgram(program2);// 灰度滤镜
    glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, tex);
    gl_drawElements(6);
    glBindFramebuffer(GL_FRAMEBUFFER, 0);


    // todo
//    glUseProgram(program);// 正常
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, tex2);
    gl_drawElements(6);
    // todo:

    /*****绘制阶段*****/
}












































