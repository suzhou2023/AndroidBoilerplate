package com.bbt2000.boilerplate.demos.gles._05_camera

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.util.Size

object CameraUtils {
    private fun choosePreviewSize(windowSize: Size, characteristics: CameraCharacteristics):
            Size {
        val supportedPreviewSizes: List<Size> =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(SurfaceTexture::class.java)
                ?.also { Log.d("CameraUtils", "Output sizes: ${it.contentToString()}") }
                ?.filter { SizeComparator.compare(it, windowSize) <= 0 }
                ?.sortedWith(SizeComparator)
                ?: emptyList()

        val length = supportedPreviewSizes.size;
        return supportedPreviewSizes.getOrElse(length / 2) { Size(0, 0) }
    }

    fun buildTargetTexture(
        containerView: SurfaceViewTest,
        characteristics: CameraCharacteristics
    ): SurfaceTexture? {
        val windowSize = Size(containerView.width, containerView.height)
        val previewSize = choosePreviewSize(windowSize, characteristics)
        Log.d("CameraUtils", "windowSize=$windowSize, previewSize=$previewSize")
        containerView.setPreviewSize(previewSize.width, previewSize.height)
        return containerView.getTexture()?.apply {
            setDefaultBufferSize(previewSize.width, previewSize.height)
        }
    }
}

internal object SizeComparator : Comparator<Size> {
    override fun compare(a: Size, b: Size): Int {
        return b.height * b.width - a.width * a.height
    }
}