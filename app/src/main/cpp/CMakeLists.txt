cmake_minimum_required(VERSION 3.22.1)


project(native VERSION 1.0)


set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("include")


add_subdirectory(opengl/01_basic)
add_subdirectory(opengl/02_3d)
add_subdirectory(ffmpeg)
add_subdirectory(hid)
#add_subdirectory(lua-test)





