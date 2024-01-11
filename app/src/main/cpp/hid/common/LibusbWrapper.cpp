/**
 *  author : suzhou
 *  date : 2024/1/3 
 *  description : 
 */


#include "LibusbWrapper.h"
#include "android_log.h"


int LibusbWrapper::prepare(int file_descriptor) {
    // 跳过设备发现流程
    int ret = libusb_set_option(nullptr, LIBUSB_OPTION_NO_DEVICE_DISCOVERY);
    if (ret != 0) {
        LOGE("libusb_set_option fail: %d", ret);
        return -1;
    }

    // 初始化libusb
    ret = libusb_init(&context);
    if (ret != 0) {
        LOGE("libusb_init fail: %d", ret);
        return -1;
    }

    // 将现有的系统设备包装成libusb设备对象
    ret = libusb_wrap_sys_device(context, (intptr_t) file_descriptor, &dev_handle);
    if (ret != 0) {
        LOGE("libusb_wrap_sys_device fail: %d", ret);
        return -1;
    }

    // 获取配置描述符
    ret = libusb_get_config_descriptor(libusb_get_device(dev_handle), 0, &config_desc);
    if (ret != 0) {
        LOGE("libusb_get_config_descriptor fail: %d", ret);
        return -1;
    }

    // 寻找中断输入端点
    for (int intf = 0; intf < config_desc->bNumInterfaces; intf++) {
        const struct libusb_interface_descriptor *intf_desc = &config_desc->interface[intf].altsetting[0];

        if (intf_desc->bInterfaceClass == LIBUSB_CLASS_HID) {
            for (int ep = 0; ep < intf_desc->bNumEndpoints; ep++) {
                if ((intf_desc->endpoint[ep].bmAttributes & LIBUSB_TRANSFER_TYPE_MASK) == LIBUSB_TRANSFER_TYPE_INTERRUPT &&
                    (intf_desc->endpoint[ep].bEndpointAddress & LIBUSB_ENDPOINT_DIR_MASK) == LIBUSB_ENDPOINT_IN) {
                    ep_int_in = intf_desc->endpoint[ep].bEndpointAddress;
                    intf_number = intf_desc->bInterfaceNumber;
                    LOGD("ep_int_in = %d", ep_int_in);
                    LOGD("intf_number = %d", intf_number);
                    break;
                }
            }
        }
        if (ep_int_in > 0) break; // 跳出外层循环
    }

    // 没有找到中断输入端点
    if (ep_int_in < 0) {
        LOGE("endpoint not found.");
        return -1;
    }

    // 使能自动卸载内核驱动
    ret = libusb_set_auto_detach_kernel_driver(dev_handle, 1);
    if (ret != 0) {
        LOGE("libusb_set_auto_detach_kernel_driver fail: %d", ret);
        return -1;
    }

    // 声明接口使用权
    ret = libusb_claim_interface(dev_handle, intf_number);
    if (ret != 0) {
        LOGE("libusb_claim_interface fail: %d", ret);
        intf_number = -1;
        return -1;
    }

    LOGD("LibusbWrapper::prepare success.");
    return 0;
}

void LibusbWrapper::release() {
    // 释放接口
    int ret{-1};
    if (intf_number >= 0) {
        ret = libusb_release_interface(dev_handle, intf_number);
        if (ret != 0) {
            LOGE("libusb_release_interface %d, fail: %d", intf_number, ret);
        } else {
            intf_number = -1;
        }
    }
    // 释放配置描述符
    if (config_desc != nullptr) {
        libusb_free_config_descriptor(config_desc);
        config_desc = nullptr;
    }
    // 关闭设备
    if (dev_handle != nullptr) {
        libusb_close(dev_handle);
        dev_handle = nullptr;
    }
    // 反初始化libusb
    if (context != nullptr) {
        libusb_exit(context);
        context = nullptr;
    }
    LOGD("LibusbWrapper::release success.");
}

int LibusbWrapper::hidRead() const {
    // 中断传输
    unsigned char buffer[1];
    int transferred;
//    int ret = libusb_interrupt_transfer(dev_handle, ep_int_in, buffer, sizeof(buffer), &transferred, 0);
//    if (ret == 0) {
//        LOGD("libusb_interrupt_transfer transferred = %d", transferred);
//        LOGD("buffer[0] = %d", buffer[0]);
//        LOGD("buffer[1] = %d", buffer[1]);
//        LOGD("buffer[2] = %d", buffer[2]);
//        LOGD("buffer[3] = %d", buffer[3]);
//
//        // 返回第一个按键值
//        for (unsigned char value: buffer) {
//            if (value > 0) return value;
//        }
//
//        return 0;
//    } else {
//        if (ret == LIBUSB_ERROR_TIMEOUT) {
//            LOGI("libusb_interrupt_transfer timeout.");
//        } else {
//            LOGE("libusb_interrupt_transfer fail: %d", ret);
//        }
//        return -1;
//    }

    int ret = libusb_control_transfer(dev_handle,
                                      LIBUSB_ENDPOINT_IN | LIBUSB_REQUEST_TYPE_CLASS | LIBUSB_RECIPIENT_INTERFACE,
                                      (LIBUSB_DT_REPORT << 8) | 0x00,
                                      0,
                                      0,
                                      buffer,
                                      sizeof(buffer),
                                      0);
    if (ret >= 0) {
        LOGD("libusb_control_transfer transferred = %d", ret);
        LOGD("buffer[0] = %d", buffer[0]);
        LOGD("buffer[1] = %d", buffer[1]);
        LOGD("buffer[2] = %d", buffer[2]);
        LOGD("buffer[3] = %d", buffer[3]);

        return 0;
    } else {
        LOGE("libusb_control_transfer fail: %d", ret);
        return -1;
    }
}

int LibusbWrapper::hidReadAsync() const {
    unsigned char buffer[2];

    struct libusb_transfer *transfer = libusb_alloc_transfer(0);
    if (transfer == nullptr) {
        LOGE("libusb_alloc_transfer error.");
        return -1;
    }

    // 填充libusb_transfer结构体
    libusb_fill_interrupt_transfer(transfer, dev_handle, ep_int_in, buffer, sizeof(buffer), transfer_callback, nullptr, 0);

    // 提交传输
    int ret = libusb_submit_transfer(transfer);
    if (ret != 0) {
        LOGE("libusb_submit_transfer error.");
        return -1;
    }
    LOGD("libusb_submit_transfer success.");

    libusb_handle_events(context);
    libusb_free_transfer(transfer);

    return 0;
}

void LibusbWrapper::transfer_callback(struct libusb_transfer *transfer) {
    LOGD("transfer_callback called.");
    LOGD("transfer->buffer[0] = %d", transfer->buffer[0]);
    LOGD("transfer->buffer[1] = %d", transfer->buffer[1]);
}


































