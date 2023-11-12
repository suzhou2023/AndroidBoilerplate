cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME opengl-bbt)

project(${PROJECT_NAME} VERSION 1.0)


set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

# 头文件
include_directories("include")

# opengl相关api编译成so
add_library(
        ${PROJECT_NAME}

        SHARED

)

# 链接库
target_link_libraries(
        ${PROJECT_NAME}

        # for openGLES
        GLESv3
        # for EGL
        EGL
        android
        jnigraphics
        # android log
        log
)











