cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME gl-basic)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")


add_library(
        ${PROJECT_NAME}

        SHARED


        01_native_window.cpp
        03_vbo.cpp
        04_ebo.cpp
        05_vao.cpp
        06_vao_vbo_ebo.cpp
        07_texture.cpp
        08_yuv.cpp
        08_yuv2.cpp
        09_fbo.cpp
        10_rgb2nv12.cpp
        10_rgb2vyuy.cpp
        jni.cpp
)


target_link_libraries( # Specifies the target library.
        ${PROJECT_NAME}

        # for openGLES
        GLESv3
        # for EGL
        EGL
        android
        jnigraphics
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})