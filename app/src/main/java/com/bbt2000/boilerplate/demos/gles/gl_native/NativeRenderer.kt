package com.bbt2000.boilerplate.demos.gles.gl_native

/**
 *  author : sz
 *  date : 2023/7/13 18:25
 *  description :
 */

class NativeRenderer {
    external fun native_OnInit()
    external fun native_OnDestroy()
    external fun native_OnSurfaceCreated()
    external fun native_OnSurfaceChanged(width: Int, height: Int)
    external fun native_OnDrawFrame()

    companion object {
        init {
            System.loadLibrary("egl_demo")
        }
    }
}
