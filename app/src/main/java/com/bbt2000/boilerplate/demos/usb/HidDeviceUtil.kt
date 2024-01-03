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
    var hidDevice: UsbDevice? = null // hid设备
    private var usbInterface: UsbInterface? = null // hid接口
    private var inEndpoint: UsbEndpoint? = null // 输入端点
    private var connection: UsbDeviceConnection? = null // usb连接
    var packetSize: Int? = null // 包大小
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
            Logger.d("device.interfaceCount = ${device.interfaceCount}")
            hidDevice = device
//            for (index in 0 until device.interfaceCount) {
//                val intf = device.getInterface(index)
//                if (intf.interfaceClass == 3) { // hid
//                    usbInterface = intf
//                    hidDevice = device
//                    break
//                }
//            }
        }

        requestPermission()

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
//        val ret = connection?.claimInterface(usbInterface, true)
//        Logger.i("claimInterface: $ret")

        if (connection != null) {
            onDeviceOpened?.invoke()
        }

        val bytes = connection?.rawDescriptors
        Logger.i("rawDescriptors size = ${bytes?.size}")
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

    fun print() {
//        Logger.d("hidDevice = $hidDevice")
//        Logger.d("manufacturerName = ${hidDevice?.manufacturerName}")
//        Logger.d("serialNumber = ${hidDevice?.serialNumber}")
//        Logger.d("configurationCount = ${hidDevice?.configurationCount}")
//        Logger.d("interfaceCount = ${hidDevice?.interfaceCount}")
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

    fun intTransfer() {
        val endpoint = UsbEndpoint.CREATOR.createFromParcel(Parcel.obtain().apply {
            writeInt(0x83)
            writeInt(0x03)
            writeInt(16)
            writeInt(32)
        }) as UsbEndpoint

        val buffer = ByteBuffer.allocate(16)
        val request = UsbRequest()
        val ret = request.initialize(connection, endpoint)
        Logger.d("ret = $ret")

        while (true) {
            if (request.queue(buffer, buffer.capacity())) {
                // 等待请求完成
                if (connection?.requestWait() === request) {
                    // 从 buffer 中读取数据
                    val data = ByteArray(buffer.remaining())
                    buffer.get(data)
                    // 处理接收到的数据
                    Logger.d("data.size = ${data.size}")
                }
            } else {
                Logger.d("queue fail")
                break
            }
        }
    }

    fun intTransfer2() {
        hidDevice ?: return
        connection ?: return

        hidRead(hidDevice!!.vendorId, hidDevice!!.productId, connection!!.fileDescriptor)
    }

    private external fun hidRead(vendorId: Int, productId: Int, fileDescriptor: Int): Int
}




































