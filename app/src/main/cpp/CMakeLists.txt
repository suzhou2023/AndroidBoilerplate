cmake_minimum_required(VERSION 3.22.1)

project(hello VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

include_directories(include)


add_library(
        gles3_0_course

        SHARED

        hello_jni.cpp
        samples/01_triangle/01_triangle.cpp
        samples/01_triangle/gl_util.cpp
        samples/01_triangle/native_render_jni.cpp
        samples/01_triangle/GlRenderContext.cpp
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
        gles3_0_course

        # 链接gles3
        GLESv3
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})