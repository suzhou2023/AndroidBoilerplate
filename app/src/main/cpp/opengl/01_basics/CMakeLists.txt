cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME gl_basics)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


add_library(
        ${PROJECT_NAME}

        SHARED

        ../jni/jni.cpp
        test.cpp
)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

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