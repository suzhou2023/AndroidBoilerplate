cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME opengl-bbt)

project(${PROJECT_NAME} VERSION 1.0)


set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("include")


add_subdirectory(01_basics)
add_subdirectory(02_camera)