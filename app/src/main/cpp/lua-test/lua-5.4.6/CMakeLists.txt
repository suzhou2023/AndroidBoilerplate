cmake_minimum_required(VERSION 3.22.1)

set(PROJECT_NAME lua546)
project(${PROJECT_NAME} VERSION 1.0)


set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)


set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/build/${ANDROID_ABI}")
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/build/${ANDROID_ABI}")

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O2 -Wall -Wextra -DLUA_COMPAT_5_3")

set(LUA_SOURCES
        src/lapi.c
        src/lauxlib.c
        src/lbaselib.c
        src/lcode.c
        src/lcorolib.c
        src/lctype.c
        src/ldblib.c
        src/ldebug.c
        src/ldo.c
        src/ldump.c
        src/lfunc.c
        src/lgc.c
        src/linit.c
        src/liolib.c
        src/llex.c
        src/lmathlib.c
        src/lmem.c
        src/loadlib.c
        src/lobject.c
        src/lopcodes.c
        src/loslib.c
        src/lparser.c
        src/lstate.c
        src/lstring.c
        src/lstrlib.c
        src/ltable.c
        src/ltablib.c
        src/ltm.c
        src/lua.c # lua.c的main是编译运行时用的
        #src/luac.c # luac.c的main是将lua代码编译成EXE用的，我们需要去掉此文件的编译
        src/lundump.c
        src/lutf8lib.c
        src/lvm.c
        src/lzio.c)


# 编译动态库
add_library(lua54 SHARED ${LUA_SOURCES})

# 编译静态库
add_library(lua54_static STATIC ${LUA_SOURCES})
























