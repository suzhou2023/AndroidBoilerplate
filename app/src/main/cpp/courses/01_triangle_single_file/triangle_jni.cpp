//
// Created by sz on 2023/7/13.
//

#include "jni.h"
#include <GLES3/gl3.h>
#include <cstdlib>

static char vShaderStr[] =
        "#version 300 es                          \n"
        "layout(location = 0) in vec4 vPosition;  \n"
        "void main()                              \n"
        "{                                        \n"
        "   gl_Position = vPosition;              \n"
        "}                                        \n";
static char fShaderStr[] =
        "#version 300 es                              \n"
        "precision mediump float;                     \n"
        "out vec4 fragColor;                          \n"
        "void main()                                  \n"
        "{                                            \n"
        "   fragColor = vec4 ( 1.0, 0.0, 0.0, 1.0 );  \n"
        "}                                            \n";

static int loadShader(int shaderType, const char *pSource) {
    int shader = 0;
    shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &pSource, nullptr);
        glCompileShader(shader);
        int compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled) {
            int infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char *buf = (char *) malloc((size_t) infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, nullptr, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}

static int createProgram(const char *pVertexShaderSource, const char *pFragShaderSource,
                         int &vertexShaderHandle, int &fragShaderHandle) {
    int program = 0;
    vertexShaderHandle = loadShader(GL_VERTEX_SHADER, pVertexShaderSource);
    if (!vertexShaderHandle) return program;
    fragShaderHandle = loadShader(GL_FRAGMENT_SHADER, pFragShaderSource);
    if (!fragShaderHandle) return program;

    program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShaderHandle);
        glAttachShader(program, fragShaderHandle);
        glLinkProgram(program);
        int linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);

        glDetachShader(program, vertexShaderHandle);
        glDeleteShader(vertexShaderHandle);
        vertexShaderHandle = 0;
        glDetachShader(program, fragShaderHandle);
        glDeleteShader(fragShaderHandle);
        fragShaderHandle = 0;
        if (linkStatus != GL_TRUE) {
            int bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char *buf = (char *) malloc((size_t) bufLength);
                if (buf) {
                    glGetProgramInfoLog(program, bufLength, nullptr, buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = 0;
        }
    }
    return program;
}

static void draw(int program) {
    GLfloat vVertices[] = {
            -0.5f, -0.0f, 0.0f,
            -0.5f, 0.5f, 0.0f,
            0.5f, 0.0f, 0.0f,
    };

    if (program == 0)
        return;

    glClear(GL_STENCIL_BUFFER_BIT | GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glClearColor(1.0, 1.0, 1.0, 1.0);

    // Use the program object
    glUseProgram(program);

    // Load the vertex data
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, vVertices);
    glEnableVertexAttribArray(0);

    glDrawArrays(GL_TRIANGLES, 0, 3);
    glUseProgram(GL_NONE);
}

static int m_ProgramObj = 0;
static int m_VertexShader{};
static int m_FragmentShader{};

JNIEXPORT void JNICALL native_OnSurfaceCreated(JNIEnv *env, jobject instance) {
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    m_ProgramObj = createProgram(vShaderStr, fShaderStr, m_VertexShader, m_FragmentShader);
}

JNIEXPORT void JNICALL
native_OnSurfaceChanged(JNIEnv *env, jobject instance, jint width, jint height) {
    glViewport(0, 0, width, height);
}

JNIEXPORT void JNICALL native_OnDrawFrame(JNIEnv *env, jobject instance) {
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    draw(m_ProgramObj);
}

// 完整类名
#define NATIVE_RENDER_CLASS_NAME "com/bbt2000/boilerplate/demos/gles/gl_native/NativeRenderer"

static JNINativeMethod g_RenderMethods[] = {
        {"native_OnSurfaceCreated", "()V",   (void *) (native_OnSurfaceCreated)},
        {"native_OnSurfaceChanged", "(II)V", (void *) (native_OnSurfaceChanged)},
        {"native_OnDrawFrame",      "()V",   (void *) (native_OnDrawFrame)},
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *methods) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods,
                             sizeof(g_RenderMethods) / sizeof(g_RenderMethods[0])) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static void unregisterNativeMethods(JNIEnv *env, const char *className) {
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return;
    }
    env->UnregisterNatives(clazz);
}

// so加载的时候会调用JNI_OnLoad
jint JNI_OnLoad(JavaVM *jvm, void *p) {
    jint jniRet = JNI_ERR;
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return jniRet;
    }
    jint regRet = registerNativeMethods(env, NATIVE_RENDER_CLASS_NAME, g_RenderMethods);
    if (regRet != JNI_TRUE) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}

void JNI_OnUnload(JavaVM *jvm, void *p) {
    JNIEnv *env = nullptr;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    if (m_ProgramObj) {
        glDeleteProgram(m_ProgramObj);
        m_ProgramObj = GL_NONE;
    }
    unregisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME);
}

