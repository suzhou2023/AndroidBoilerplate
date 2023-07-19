package com.bbt2000.boilerplate.demos.gles._04_yuv

import android.content.Context
import android.content.res.AssetManager
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                loadYuv(holder.surface, context.assets)
            }
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private external fun loadYuv(surface: Any, assetManager: AssetManager)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("yuv")
        }
    }
}
