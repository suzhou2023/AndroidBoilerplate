/**
 *  author : sz
 *  date : 2024/1/2
 *  description : 
 */


#include "hid.h"
#include "android_log.h"
#include "libusb/libusb.h"


unsigned char buffer[1000];

int hidRead(int file_descriptor) {
    // 跳过设备发现流程
    int ret = libusb_set_option(nullptr, LIBUSB_OPTION_NO_DEVICE_DISCOVERY, nullptr);
    if (ret != 0) {
        LOGE("libusb_set_option fail: %d", ret);
        return -1;
    }

    // 初始化
    libusb_context *context{nullptr};
    ret = libusb_init(&context);
    if (ret != 0) {
        LOGE("libusb_init fail: %d", ret);
        return -1;
    }

    libusb_device_handle *dev_handle{nullptr};
    ret = libusb_wrap_sys_device(context, (intptr_t) file_descriptor, &dev_handle);
    if (ret != 0) {
        LOGE("libusb_wrap_sys_device fail: %d", ret);
        return -1;
    }

    // 获取配置描述符
    struct libusb_config_descriptor *config_desc;
    ret = libusb_get_config_descriptor(libusb_get_device(dev_handle), 0, &config_desc);
    if (ret != 0) {
        LOGE("libusb_get_config_descriptor fail: %d", ret);
        return -1;
    }

    // 寻找中断输入端点
    int intf_number{-1};
    int ep_int_in{-1};
    for (int intf = 0; intf < config_desc->bNumInterfaces; intf++) {
        const struct libusb_interface_descriptor *intf_desc = &config_desc->interface[intf].altsetting[0];
        intf_number = intf_desc->bInterfaceNumber;
        if (intf_desc->bInterfaceClass == 3) {
            for (int ep = 0; ep < intf_desc->bNumEndpoints; ep++) {
                if ((intf_desc->endpoint[ep].bmAttributes & 3) == LIBUSB_TRANSFER_TYPE_INTERRUPT &&
                    (intf_desc->endpoint[ep].bEndpointAddress & 0x80) == LIBUSB_ENDPOINT_IN) {
                    ep_int_in = intf_desc->endpoint[ep].bEndpointAddress;
                    LOGD("ep_int_in = %d", ep_int_in);
                    break;
                }
            }
        }
    }

    // 释放配置描述符
    libusb_free_config_descriptor(config_desc);

    // 没有找到中断输入端点，释放资源
    if (ep_int_in < 0) {
        libusb_close(dev_handle);
        libusb_exit(context);
        return -1;
    }


    ret = libusb_set_auto_detach_kernel_driver(dev_handle, 1);
    if (ret != 0) {
        LOGE("libusb_set_auto_detach_kernel_driver fail: %d", ret);
        return -1;
    }

    ret = libusb_claim_interface(dev_handle, intf_number);
    if (ret != 0) {
        LOGE("libusb_claim_interface fail: %d", ret);
        return -1;
    }

    unsigned char buffer[16];
    int transferred;
    LOGD("libusb_interrupt_transfer=======");

    while (1) {
        ret = libusb_interrupt_transfer(dev_handle, ep_int_in, buffer, 16, &transferred, 0);
        if (ret != 0) {
            LOGE("libusb_interrupt_transfer fail: %d", ret);
            return -1;
        }

        LOGD("libusb_interrupt_transfer transferred = %d", transferred);
    }
}





































