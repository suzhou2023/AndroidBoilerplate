cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME gl-3d)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/../../include/gl")
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/../../../jniLibs/${ANDROID_ABI}")


add_library(
        ${PROJECT_NAME}

        SHARED

        jni.cpp
        01_tex_3d.cpp
        02_render_cube.cpp
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





































