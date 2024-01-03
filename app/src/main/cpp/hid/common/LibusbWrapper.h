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
    libusb_context *context{nullptr};
};

#endif //ANDROIDBOILERPLATE_LIBUSBWRAPPER_H
