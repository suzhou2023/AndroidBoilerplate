package com.bbt2000.boilerplate.demos.opengles

/**
 *  author : sz
 *  date : 2023/7/13 18:25
 *  description :
 */

class NativeRender {
    external fun native_OnInit()
    external fun native_OnDestroy()
    external fun native_OnSurfaceCreated()
    external fun native_OnSurfaceChanged(width: Int, height: Int)
    external fun native_OnDrawFrame()

    companion object {
        init {
            System.loadLibrary("gles3_0_course")
        }
    }
}
