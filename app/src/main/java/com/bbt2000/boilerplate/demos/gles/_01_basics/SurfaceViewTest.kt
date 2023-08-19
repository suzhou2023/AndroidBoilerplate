package com.bbt2000.boilerplate.demos.gles._01_basics

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.Surface
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
//        nativeApiTest(holder.surface)

//        CoroutineScope(Dispatchers.Default).launch {
//            withContext(Dispatchers.IO) {
//                nativeLoadYuv(holder.surface, context.assets)
//            }
//        }

//        val bitmap = context.resources.getDrawable(R.drawable.wall).toBitmap()
//        nativeTexture(holder.surface, bitmap)


        nativePrintGLSL(context.assets)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private external fun nativeApiTest(surface: Surface)
    private external fun nativeTexture(surface: Surface, bitmap: Bitmap)
    private external fun nativeLoadYuv(surface: Surface, assetManager: AssetManager)
    private external fun nativePrintGLSL(assetManager: AssetManager)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("gl_basics")
        }
    }
}
