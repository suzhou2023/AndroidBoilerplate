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
import android.hardware.usb.UsbRequest
import android.os.Parcel
import androidx.core.content.ContextCompat
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.orhanobut.logger.Logger
import java.nio.ByteBuffer


/**
 *  author : sz
 *  date : 2023/11/28
 *  description :
 */
object HidDeviceUtil {
    const val ACTION_USB_PERMISSION = "com.android.hardware.USB_PERMISSION"
    private val usbManager by lazy { ContextCompat.getSystemService(ContextUtil.application, UsbManager::class.java) }
    var usbDevice: UsbDevice? = null // hid设备
    private var connection: UsbDeviceConnection? = null // usb连接
    private var onDeviceOpened: (() -> Unit)? = null // 设备打开回调

    init {
        System.loadLibrary("bbt-hid")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Logger.i("intent.action = ${intent.action}")

            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            Logger.i("device = $device")

            if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                Logger.i("ACTION_USB_DEVICE_ATTACHED")
                if (usbDevice == null) {
                    enumDevice()
                }
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                Logger.i("ACTION_USB_DEVICE_DETACHED")
                if (usbDevice != null) {
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

    fun enumDevice(onDeviceOpened: (() -> Unit)? = null) {
        if (onDeviceOpened != null) {
            this.onDeviceOpened = onDeviceOpened
        }
        val deviceList = usbManager?.deviceList
        Logger.d("deviceList = $deviceList")
        if (deviceList.isNullOrEmpty()) return

        for (device in deviceList.values) {
            usbDevice = device
        }

        requestPermission()
    }

    fun registerReceiver() {
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        ContextUtil.application.registerReceiver(receiver, filter)
    }

    fun unregisterReceiver() {
        ContextUtil.application.unregisterReceiver(receiver)
    }

    private fun requestPermission() {
        usbManager ?: return
        if (usbManager!!.hasPermission(usbDevice)) {
            Logger.i("hasPermission.")
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
    fun openDevice() {
        connection = usbManager?.openDevice(usbDevice)
        Logger.i("openDevice success: ${connection != null}")

        if (connection != null) {
            onDeviceOpened?.invoke()
        }

        val bytes = connection?.rawDescriptors
        Logger.i("rawDescriptors size = ${bytes?.size}")
    }

    // 关闭设备
    fun closeDevice() {
        connection?.close()
        Logger.i("connection closed.")
        connection = null
        usbDevice = null
    }


    fun controlTransfer() {
        val array = ByteArray(1024)
//        val ret = connection?.controlTransfer(UsbConstants.USB_DIR_IN, 0x06, 1, 0, array, array.size, 0)
        val ret = connection?.controlTransfer(0x80, 0x06, 0x02 shl 8, 0, array, array.size, 0) ?: -1
        Logger.d("ret = $ret")
        if (ret > 0) {
            Logger.d("array[0] = ${array[0]}")
            Logger.d("array[1] = ${array[1]}")
        }
    }

    fun bulkTransfer() {
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

    fun hidRead() {
        usbDevice ?: return
        connection ?: return

        hidRead(usbDevice!!.vendorId, usbDevice!!.productId, connection!!.fileDescriptor)
    }

    private external fun hidRead(vendorId: Int, productId: Int, fileDescriptor: Int)
}




































