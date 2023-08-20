cmake_minimum_required(VERSION 3.22.1)

project(gles3_0_course VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("opengl/include")


add_subdirectory(opengl/01_basics)