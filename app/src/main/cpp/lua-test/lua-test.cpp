/**
 *  author : sz
 *  date : 2024/1/11
 *  description : 
 */

#include "lua-test.h"

#include "src/lua.hpp"


int test() {
    lua_State *state = luaL_newstate();
    if (state == nullptr) {
        return -1;
    }

}