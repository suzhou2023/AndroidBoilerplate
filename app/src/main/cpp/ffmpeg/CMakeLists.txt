cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME ffmpeg-bbt)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(FFMPEG_JNILIBS_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/../../jniLibs/${ANDROID_ABI}")


include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")

link_directories("${FFMPEG_JNILIBS_DIRECTORY}")


add_library(
        ${PROJECT_NAME}

        SHARED

        jni.cpp
        rtsp/stream.cpp
)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


target_link_libraries( # Specifies the target library.
        ${PROJECT_NAME}

        avcodec
        avformat
        avutil
        avdevice
        avfilter
        swresample
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})























