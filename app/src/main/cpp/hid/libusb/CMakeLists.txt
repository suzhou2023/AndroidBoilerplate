cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME usb1026)
project(${PROJECT_NAME} VERSION 1.0.26)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/build/${ANDROID_ABI}")
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/build/${ANDROID_ABI}")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fvisibility=hidden -pthread")


include_directories("${CMAKE_CURRENT_SOURCE_DIR}")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/os")


add_library(
        ${PROJECT_NAME}
        SHARED
        core.c
        descriptor.c
        hotplug.c
        io.c
        sync.c
        strerror.c
        os/linux_usbfs.c
        os/events_posix.c
        os/threads_posix.c
        os/linux_netlink.c
)


target_link_libraries( # Specifies the target library.
        ${PROJECT_NAME}

        log)




















