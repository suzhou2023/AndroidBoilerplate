/**
 *  author : sz
 *  date : 2023/11/21
 *  description : 
 */

#include "01_tex_3d.h"

#include <GLES3/gl3.h>
#include "glm/glm.hpp"
#include "glm/ext/matrix_transform.hpp"
#include "glm/ext/matrix_clip_space.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "gl_util.h"

using namespace glm;


void tex_3d(JNIEnv *env, GLContext *glContext, int pIndex, int win_w, int win_h, jobject bitmap) {
    // 模型矩阵，将局部坐标转换为世界坐标
    mat4 model = mat4(1.0f);
    // 观察矩阵，确定物体与摄像机的相对位置
    mat4 view = mat4(1.0f);
    // 投影矩阵，实现近大远小的效果
    mat4 proj = mat4(1.0f);

    // 沿x轴旋转
    rotate(model, radians(-45.0f), vec3(1.0f, 0.0f, 0.0f));
    // 平移
    translate(view, vec3(0.0f, 0.0f, -3.0f));
    // 视场角45度，近平面0.1，远平面100
    proj = perspective(radians(45.0f), (float) win_w / win_h, 0.1f, 100.0f);

    // 矩阵赋值
    GLint model_loc = glGetUniformLocation(glContext->program[pIndex], "m_model");
    GLint view_loc = glGetUniformLocation(glContext->program[pIndex], "m_view");
    GLint proj_loc = glGetUniformLocation(glContext->program[pIndex], "m_proj");
    glUniformMatrix4fv(model_loc, 1, GL_FALSE, value_ptr(model));
    glUniformMatrix4fv(view_loc, 1, GL_FALSE, value_ptr(view));
    glUniformMatrix4fv(proj_loc, 1, GL_FALSE, value_ptr(proj));

    gl_genTex2D(&glContext->texture[0]);
    glUniform1i(glGetUniformLocation(glContext->program[pIndex], "tex2d"), 0);
    glActiveTexture(GL_TEXTURE0);

    uint32_t bitmap_w, bitmap_h;
    gl_texImage2D(env, bitmap, &bitmap_w, &bitmap_h);

    gl_drawElements(6);
    eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);
}




































