cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME gl-basic)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(JNILIBS_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/../../../jniLibs/${ANDROID_ABI}")

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")
link_directories("${JNILIBS_DIRECTORY}")


add_library(
        ${PROJECT_NAME}

        SHARED

        jni.cpp
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
)


target_link_libraries(
        ${PROJECT_NAME}

        gl-common
        # for openGLES
        GLESv3
        # for EGL
        EGL
        android
        jnigraphics
        # android log
        log
)





































