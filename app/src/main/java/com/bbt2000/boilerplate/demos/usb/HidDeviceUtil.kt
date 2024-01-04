package com.bbt2000.boilerplate.demos.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbManager
import android.os.Parcel
import androidx.core.content.ContextCompat
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.orhanobut.logger.Logger


/**
 *  author : sz
 *  date : 2023/11/28
 *  description :
 */
object HidDeviceUtil {
    private val usbManager by lazy { ContextCompat.getSystemService(ContextUtil.application, UsbManager::class.java) }
    var usbDevice: UsbDevice? = null // hid设备
    var connection: UsbDeviceConnection? = null // usb连接
    const val ACTION_USB_PERMISSION = "com.bbt2000.USB_PERMISSION"
    private var libusbWrapper: Long = 0 // LibusbWrapper c++指针
    private var onDeviceOpened: ((Boolean) -> Unit)? = null // 设备打开/关闭回调

    init {
        System.loadLibrary("bbt-hid")
    }


    fun enumDevice(onDeviceOpened: ((Boolean) -> Unit)? = null) {
        if (usbDevice == null) {
            this.onDeviceOpened = onDeviceOpened
            val deviceList = usbManager?.deviceList
            Logger.i("deviceList = $deviceList")
            if (deviceList.isNullOrEmpty()) return

            usbDevice = deviceList.values.iterator().next()
            requestPermission()
        }
    }

    private fun requestPermission() {
        usbManager ?: return
        if (usbManager!!.hasPermission(usbDevice)) {
            Logger.i("hasPermission: ${usbDevice?.deviceName}")
            openDevice()
        } else {
            // 使用FLAG_MUTABLE标志
            val pendingIntent = PendingIntent.getBroadcast(
                ContextUtil.application,
                0,
                Intent(ACTION_USB_PERMISSION),
                PendingIntent.FLAG_MUTABLE
            )
            usbManager!!.requestPermission(usbDevice, pendingIntent)
        }
    }

    // 打开设备
    private fun openDevice() {
        connection = usbManager?.openDevice(usbDevice)
        if (connection != null) {
            Logger.i("openDevice success: ${usbDevice?.deviceName}")
            if (libusbWrapper <= 0) {
                libusbWrapper = libusbPrepare(connection!!.fileDescriptor)
            }
            if (libusbWrapper > 0) {
                onDeviceOpened?.invoke(true)
            }
        } else {
            Logger.i("openDevice fail: ${usbDevice?.deviceName}")
        }
    }

    // 关闭设备连接
    private fun closeDevice() {
        if (libusbWrapper > 0) {
            libusbRelease(libusbWrapper)
            libusbWrapper = 0
        }
        connection?.close()
        connection = null
        usbDevice = null
        onDeviceOpened?.invoke(false)
        Logger.i("connection closed.")
    }

    fun controlTransferTest() {
        val array = ByteArray(1024)
        val ret = connection?.controlTransfer(0x80, 0x06, 0x02 shl 8, 0, array, array.size, 0) ?: -1
        Logger.d("ret = $ret")
        if (ret > 0) {
            Logger.d("array[0] = ${array[0]}")
            Logger.d("array[1] = ${array[1]}")
        }
    }

    fun bulkTransferTest() {
        val endpoint = UsbEndpoint.CREATOR.createFromParcel(Parcel.obtain().apply {
            writeInt(0x83)
            writeInt(0x03)
            writeInt(16)
            writeInt(6)
        }) as UsbEndpoint
        val array = ByteArray(16)
        val ret = connection?.bulkTransfer(endpoint, array, array.size, 0)
        Logger.d("ret = $ret")
    }

    fun hidReadTest() {
        connection ?: return
        if (libusbWrapper > 0) {
            libusbHidRead(libusbWrapper)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Logger.i("intent.action = ${intent.action}")

            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                if (usbDevice == null) {
                    enumDevice(onDeviceOpened)
                }
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                if (device == usbDevice) {
                    closeDevice()
                }
            } else if (intent.action == ACTION_USB_PERMISSION) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    Logger.i("EXTRA_PERMISSION_GRANTED true.")
                    if (device == usbDevice) {
                        openDevice()
                    }
                } else {
                    Logger.i("EXTRA_PERMISSION_GRANTED false.")
                }
            }
        }
    }

    fun registerReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        ContextUtil.application.registerReceiver(receiver, filter)
    }

    fun unregisterReceiver() {
        ContextUtil.application.unregisterReceiver(receiver)
        closeDevice()
    }


    private external fun libusbPrepare(fileDescriptor: Int): Long
    private external fun libusbRelease(libusbWrapper: Long)
    private external fun libusbHidRead(libusbWrapper: Long)
}




































