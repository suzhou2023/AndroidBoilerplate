cmake_minimum_required(VERSION 3.22.1)

project("compat")

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/")


file(GLOB_RECURSE SOURCE_FILES "${CMAKE_CURRENT_SOURCE_DIR}/*.c")


add_library(
        compat

        SHARED

        ${SOURCE_FILES}
)
