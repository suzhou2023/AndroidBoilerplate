cmake_minimum_required(VERSION 3.22.1)


set(PROJECT_NAME lua-test)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("${CMAKE_CURRENT_SOURCE_DIR}")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/lua-5.4.6/")


add_library(
        ${PROJECT_NAME}

        SHARED

        lua-test.cpp
)


target_link_libraries(
        ${PROJECT_NAME}

        # android log
        log)


add_subdirectory(lua-5.4.6)





















