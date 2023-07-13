package com.bbt2000.boilerplate.demos.opengles

/**
 *  author : sz
 *  date : 2023/7/13 14:31
 *  description :
 */

class JniDemo {
    init {
        System.loadLibrary("hello")
    }

    external fun sayHello()
    external fun stringFromNative(): String
}