package com.bbt2000.boilerplate.demos.ffmpeg

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import com.bbt2000.boilerplate.demos.ffmpeg.jni.Jni
import com.bbt2000.gles.base.BaseSurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        CoroutineScope(Dispatchers.IO).launch {
            Jni.openStream("rtsp://192.168.101.12:8554/stream")
        }
    }
}

















