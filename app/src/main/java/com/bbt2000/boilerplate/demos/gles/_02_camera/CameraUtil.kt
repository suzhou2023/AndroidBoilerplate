package com.bbt2000.boilerplate.demos.gles._02_camera

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.util.Size

object CameraUtil {
    fun choosePreviewSize(windowSize: Size, characteristics: CameraCharacteristics): Size? {
        val supportedPreviewSizes: List<Size>? =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(SurfaceTexture::class.java)
                ?.also { Log.d("CameraUtil", "Output sizes: ${it.contentToString()}") }
                ?.filter { SizeComparator.compare(it, windowSize) <= 0 }
                ?.sortedWith(SizeComparator)

        val length = supportedPreviewSizes?.size ?: 0
        return supportedPreviewSizes?.getOrElse(length / 2) { null }
    }
}

internal object SizeComparator : Comparator<Size> {
    override fun compare(a: Size, b: Size): Int {
        return b.height * b.width - a.width * a.height
    }
}