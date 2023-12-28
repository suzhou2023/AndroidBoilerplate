package com.bbt2000.boilerplate.demos.gles._01_basic

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.gles._01_basic.test.apiTest
import com.bbt2000.boilerplate.demos.gles._01_basic.test.loadYuv
import com.bbt2000.boilerplate.demos.gles._01_basic.test.loadYuvVideo
import com.bbt2000.boilerplate.demos.gles._01_basic.test.rgb2nv12
import com.bbt2000.boilerplate.demos.gles._01_basic.test.rgb2vyuy
import com.bbt2000.boilerplate.demos.gles._01_basic.test.texture
import com.bbt2000.gles.base.BaseSurfaceView
import com.bbt2000.gles.jni.JniGL
import com.orhanobut.logger.Logger


/**
 *  author : suzhou
 *  date : 2023/7/16
 *  description :
 */
class SurfaceViewTest @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : BaseSurfaceView(context, attrs) {

    override fun surfaceCreated(holder: SurfaceHolder) {
        super.surfaceCreated(holder)
        glHandler.post {
            JniGL.loadVertices(glContext)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        super.surfaceChanged(holder, format, width, height)
        glHandler.post {
            rgb2nv12(glContext)
        }
    }
}


















