/**
 *  author : sz
 *  date : 2023/11/21
 *  description : 
 */


#include "02_render_cube.h"


#include <GLES3/gl3.h>
#include <thread>
#include "vertices.h"
#include "glm/glm.hpp"
#include "glm/ext/matrix_transform.hpp"
#include "glm/ext/matrix_clip_space.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "gl_util.h"

using namespace glm;


void render_cube(GLContext *glContext) {
    glContext->loadVertices2(vertices, 8, 6, indices, 36);

    glEnable(GL_DEPTH_TEST);
    glEnable(GL_CULL_FACE);


    float f = 0.f;
    while (f >= 0) {
        // 模型矩阵，将局部坐标转换为世界坐标
        mat4 model = mat4(1.0f);
        // 观察矩阵，确定物体与摄像机的相对位置
        mat4 view = mat4(1.0f);
        // 投影矩阵，实现近大远小的效果
        mat4 proj = mat4(1.0f);

        // 沿x轴旋转
        model = rotate(model, radians(f), vec3(1.0f, 1.0f, 1.0f));
        // 平移
        view = translate(view, vec3(0.0f, 0.0f, -5.f));
        // 视场角45度，近平面0.1，远平面100
        proj = perspective(radians(45.0f), (float) glContext->windowW / glContext->windowH, 0.1f, 100.0f);

        // 矩阵赋值
        GLint model_loc = glGetUniformLocation(glContext->program[0], "m_model");
        GLint view_loc = glGetUniformLocation(glContext->program[0], "m_view");
        GLint proj_loc = glGetUniformLocation(glContext->program[0], "m_proj");
        glUniformMatrix4fv(model_loc, 1, GL_FALSE, value_ptr(model));
        glUniformMatrix4fv(view_loc, 1, GL_FALSE, value_ptr(view));
        glUniformMatrix4fv(proj_loc, 1, GL_FALSE, value_ptr(proj));


        gl_drawElements(36);
        eglSwapBuffers(glContext->eglDisplay, glContext->eglSurface[0]);

        std::this_thread::sleep_for(std::chrono::milliseconds(10));
        f++;
    }
}














































