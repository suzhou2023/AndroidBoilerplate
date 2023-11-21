package com.bbt2000.boilerplate.demos.gles._03_3d

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.gles._03_3d.test.renderCube
import com.bbt2000.boilerplate.demos.gles._03_3d.test.tex3d
import com.bbt2000.gles.base.BaseSurfaceView
import com.bbt2000.gles.jni.JniGL

/**
 *  author : suzhou
 *  date : 2023/11/12
 *  description :
 */
class SurfaceViewTest(
    context: Context,
    attrs: AttributeSet? = null
) : BaseSurfaceView(context, attrs) {

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        super.surfaceChanged(holder, format, width, height);
        glHandler.post {
            renderCube(glContext)
        }
    }
}



































