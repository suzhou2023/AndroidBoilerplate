package com.bbt2000.boilerplate.demos.usb

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
import com.bbt2000.boilerplate.common.util.ContextUtil
import com.orhanobut.logger.Logger

/**
 *  author : sz
 *  date : 2023/11/28
 *  description :
 */
object HidDeviceUtil {
    const val ACTION_USB_PERMISSION = "com.android.bbt.USB_PERMISSION"
    private val usbManager by lazy { ContextCompat.getSystemService(ContextUtil.application, UsbManager::class.java) }
    var hidDevice: UsbDevice? = null // hid设备
    private var usbInterface: UsbInterface? = null // hid接口
    private var inEndpoint: UsbEndpoint? = null // 输入端点
    private var connection: UsbDeviceConnection? = null // usb连接
    var packetSize: Int? = null // 包大小
    private var onDeviceOpened: (() -> Unit)? = null // 设备打开回调

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            Logger.i("device = $device")

            if (intent.action == UsbManager.ACTION_USB_DEVICE_ATTACHED) {
                Logger.i("ACTION_USB_DEVICE_ATTACHED")
                if (hidDevice == null) {
                    enumDevice()
                }
            } else if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                Logger.i("ACTION_USB_DEVICE_DETACHED")
                if (hidDevice != null) {
                    closeDevice()
                }
            } else if (intent.action == ACTION_USB_PERMISSION) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    Logger.i("EXTRA_PERMISSION_GRANTED true.")
                    if (device == hidDevice) {
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
            for (index in 0 until device.interfaceCount) {
                val interface_ = device.getInterface(index)
                if (interface_.interfaceClass == 3) { // hid
                    usbInterface = interface_
                    hidDevice = device
                    break
                }
            }
        }

        if (usbInterface == null) {
            Logger.e("no device.")
            return
        }

        for (index in 0 until usbInterface!!.endpointCount) {
            val endpoint = usbInterface!!.getEndpoint(index)
            if (endpoint.direction == UsbConstants.USB_DIR_IN && endpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT) {
                inEndpoint = endpoint
                packetSize = endpoint.maxPacketSize
                Logger.i("found inEndpoint.")
                break
            }
        }

        if (inEndpoint != null) {
            requestPermission()
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
    }

    private fun requestPermission() {
        usbManager ?: return
        if (usbManager!!.hasPermission(hidDevice)) {
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
            usbManager!!.requestPermission(hidDevice, pendingIntent)
        }
    }

    // 打开设备
    fun openDevice() {
        connection = usbManager?.openDevice(hidDevice)
        Logger.i("openDevice success: ${connection != null}")
        val ret = connection?.claimInterface(usbInterface, true)
        Logger.i("claimInterface: $ret")
        if (ret == true) {
            onDeviceOpened?.invoke()
        }
    }

    // 关闭设备
    fun closeDevice() {
        val ret = connection?.releaseInterface(usbInterface)
        Logger.i("releaseInterface success: $ret")
        connection?.close()
        Logger.i("connection closed.")
        connection = null
        hidDevice = null
    }

    // 读取数据
    fun read(array: ByteArray, size: Int, timeout: Int = 0): Int {
        if (connection != null) {
            return connection!!.bulkTransfer(inEndpoint, array, size, timeout)
        }
        return -1
    }
}




































