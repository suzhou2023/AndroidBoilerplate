package com.bbt2000.boilerplate.demos.gles._01_glsurfaceview_egl

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewTest(context: Context, attrs: AttributeSet? = null) :
    SurfaceView(context, attrs), SurfaceHolder.Callback {

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        /*****不通过EGL直接操作native window来控制屏幕颜色*****/
//        drawSurfaceWithoutEGL(holder.surface, 250)
        /*****不通过EGL直接操作native window来控制屏幕颜色*****/

        /*****通过在native层手动配置EGL环境，来操作屏幕*****/
        drawWithNativeEGL(holder.surface)
        /*****通过在native层手动配置EGL环境，来操作屏幕*****/
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private external fun drawSurfaceWithoutEGL(surface: Any, color: Int)
    private external fun drawWithNativeEGL(surface: Any)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("glsurfaceview_egl")
        }
    }
}
