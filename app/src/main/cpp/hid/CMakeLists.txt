cmake_minimum_required(VERSION 3.22.1)


set(PROJECT_NAME bbt-hid)
project(${PROJECT_NAME} VERSION 1.0)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


include_directories("${CMAKE_CURRENT_SOURCE_DIR}")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/libusb")
link_directories("${CMAKE_CURRENT_SOURCE_DIR}/libusb/build/${ANDROID_ABI}")


add_library(
        ${PROJECT_NAME}

        SHARED

        jni.cpp
        hid.cpp
        common/LibusbWrapper.cpp
)


target_link_libraries( # Specifies the target library.
        ${PROJECT_NAME}

        usb1026
        # android log
        log)


add_subdirectory(libusb)





















