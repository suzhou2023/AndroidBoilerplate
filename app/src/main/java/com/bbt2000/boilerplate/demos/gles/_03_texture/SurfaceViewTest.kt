package com.bbt2000.boilerplate.demos.gles._03_texture

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.drawable.toBitmap
import com.bbt2000.boilerplate.R


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
        val bitmap = resources.getDrawable(R.drawable.wy_300x200).toBitmap()
        drawTexture(holder.surface, bitmap)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    private external fun drawTexture(surface: Any, bitmap: Bitmap)


    companion object {
        const val TAG = "SurfaceViewTest"

        init {
            System.loadLibrary("texture")
        }
    }
}
