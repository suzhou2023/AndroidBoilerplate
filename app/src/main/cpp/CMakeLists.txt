cmake_minimum_required(VERSION 3.22.1)

project(gles3_0_course VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

add_subdirectory(courses/01_egl)
add_subdirectory(courses/02_gl_api)
add_subdirectory(courses/03_texture)
add_subdirectory(courses/04_camera)