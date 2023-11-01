cmake_minimum_required(VERSION 3.22.1)


project(native VERSION 1.0)


set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("${CMAKE_CURRENT_SOURCE_DIR}/include")


add_subdirectory(ffmpeg)
add_subdirectory(opengl)





