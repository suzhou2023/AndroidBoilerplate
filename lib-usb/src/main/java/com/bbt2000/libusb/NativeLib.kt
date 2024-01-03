package com.bbt2000.libusb

class NativeLib {

    /**
     * A native method that is implemented by the 'libusb' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'libusb' library on application startup.
        init {
            System.loadLibrary("libusb")
        }
    }
}