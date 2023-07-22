package com.bbt2000.boilerplate.demos.gles._05_camera

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.util.Log
import android.util.Size
import android.view.TextureView

object CameraUtils {

    /** Return the biggest preview size available which is smaller than the window */
    private fun findBestPreviewSize(windowSize: Size, characteristics: CameraCharacteristics):
            Size {
        val supportedPreviewSizes: List<Size> =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(SurfaceTexture::class.java)
                ?.filter { SizeComparator.compare(it, windowSize) >= 0 }
                ?.sortedWith(SizeComparator)
                ?: emptyList()

        val array: List<Size> =
            characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                ?.getOutputSizes(SurfaceTexture::class.java)
                ?.filter { true }
                ?: emptyList()
        Log.d("CameraUtils", "array = ${array}")
        Log.d("CameraUtils", "array[0] = ${array[0]}")
        Log.d("CameraUtils", "supportedPreviewSizes[0] = ${supportedPreviewSizes[0]}")

        return supportedPreviewSizes.getOrElse(0) { Size(0, 0) }
    }

    /**
     * Returns a new SurfaceTexture that will be the target for the camera preview
     */
    fun buildTargetTexture(
        containerView: TextureView,
        characteristics: CameraCharacteristics
    ): SurfaceTexture? {

        /*** Codelab --> Change this function to handle viewfinder rotation and scaling ***/

        val windowSize = Size(containerView.width, containerView.height)
        val previewSize = findBestPreviewSize(windowSize, characteristics)
        Log.d("CameraUtils", "windowSize: $windowSize")
        Log.d("CameraUtils", "previewSize: $previewSize")
        return containerView.surfaceTexture?.apply {
            setDefaultBufferSize(previewSize.width, previewSize.height)
        }
    }

    fun buildTargetTexture(
        containerView: SurfaceViewTest,
        characteristics: CameraCharacteristics
    ): SurfaceTexture? {
        val windowSize = Size(containerView.width, containerView.height)
        val previewSize = findBestPreviewSize(windowSize, characteristics)
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