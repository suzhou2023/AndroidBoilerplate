/**
 *  author : sz
 *  date : 2024/1/2
 *  description : 
 */


#include "hid.h"
#include "android_log.h"
#include "libusb/libusb.h"
#include "common/LibusbWrapper.h"

/**
 * hid按键读取
 * @param wrapper
 * @param file_descriptor
 */
void hidRead(LibusbWrapper *wrapper, int file_descriptor) {
    // 跳过设备发现流程
    int ret = libusb_set_option(nullptr, LIBUSB_OPTION_NO_DEVICE_DISCOVERY);
    if (ret != 0) {
        LOGE("libusb_set_option fail: %d", ret);
        return;
    }

    libusb_context *context{nullptr};
    libusb_device_handle *dev_handle{nullptr};
    struct libusb_config_descriptor *config_desc;
    int intf_number{-1};
    int ep_int_in{-1};

    // 初始化libusb
    ret = libusb_init(&context);
    if (ret != 0) {
        LOGE("libusb_init fail: %d", ret);
        goto FREE;
    }

    // 将现有的系统设备包装成libusb设备对象
    ret = libusb_wrap_sys_device(context, (intptr_t) file_descriptor, &dev_handle);
    if (ret != 0) {
        LOGE("libusb_wrap_sys_device fail: %d", ret);
        goto FREE;
    }

    // 获取配置描述符
    ret = libusb_get_config_descriptor(libusb_get_device(dev_handle), 0, &config_desc);
    if (ret != 0) {
        LOGE("libusb_get_config_descriptor fail: %d", ret);
        goto FREE;
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
    }

    // 没有找到中断输入端点
    if (ep_int_in < 0) {
        LOGE("endpoint not found.");
        goto FREE;
    }

    // 使能自动卸载内核驱动
    ret = libusb_set_auto_detach_kernel_driver(dev_handle, 1);
    if (ret != 0) {
        LOGE("libusb_set_auto_detach_kernel_driver fail: %d", ret);
        goto FREE;
    }

    // 声明接口使用权
    ret = libusb_claim_interface(dev_handle, intf_number);
    if (ret != 0) {
        LOGE("libusb_claim_interface fail: %d", ret);
        intf_number = -1;
        goto FREE;
    }

    // 中断传输
    unsigned char buffer[16];
    int transferred;
    ret = libusb_interrupt_transfer(dev_handle, ep_int_in, buffer, 16, &transferred, 0);
    if (ret != 0) {
        LOGE("libusb_interrupt_transfer fail: %d", ret);
    }
    LOGD("libusb_interrupt_transfer transferred = %d", transferred);

    FREE:
    // 释放接口
    if (intf_number >= 0) {
        libusb_release_interface(dev_handle, intf_number);
    }
    // 释放配置描述符
    if (config_desc != nullptr) {
        libusb_free_config_descriptor(config_desc);
    }
    // 关闭设备
    if (dev_handle != nullptr) {
        libusb_close(dev_handle);
    }
    // 反初始化libusb
    if (context != nullptr) {
        libusb_exit(context);
    }
}





































