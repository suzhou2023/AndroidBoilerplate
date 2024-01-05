/**
 *  author : suzhou
 *  date : 2024/1/3 
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_LIBUSBWRAPPER_H
#define ANDROIDBOILERPLATE_LIBUSBWRAPPER_H


#include "libusbi.h"

class LibusbWrapper {
public:

    int prepare(int file_descriptor);

    void release();

    int hidRead() const;

    int hidReadAsync() const;

    static void transfer_callback(struct libusb_transfer *transfer);

    LibusbWrapper() {}

    ~LibusbWrapper() {}

    libusb_context *context{nullptr};
    libusb_device_handle *dev_handle{nullptr};
    struct libusb_config_descriptor *config_desc{nullptr};
    int intf_number{-1};
    int ep_int_in{-1};
};

#endif //ANDROIDBOILERPLATE_LIBUSBWRAPPER_H





























