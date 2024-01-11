cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME ffmpeg-bbt)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/jniLibs/${ANDROID_ABI}")
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/../../jniLibs/${ANDROID_ABI}")


add_library(
        ${PROJECT_NAME}

        SHARED

        jni.cpp
        common/FFContext.cpp
        common/gl.cpp
)


target_link_libraries( # Specifies the target library.
        ${PROJECT_NAME}

        # ffmpeg
        avcodec
        avformat
        avutil
        avdevice
        avfilter
        swresample
        # opengl
        gl-common
        # openGLES
        GLESv3
        # EGL
        EGL
        android
        jnigraphics
        # android log
        log)






















