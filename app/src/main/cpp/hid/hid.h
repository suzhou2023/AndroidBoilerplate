/**
 *  author : sz
 *  date : 2024/1/2
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_HID_H
#define ANDROIDBOILERPLATE_HID_H


#include "common/LibusbWrapper.h"

void hidRead(LibusbWrapper *wrapper, int file_descriptor);

#endif //ANDROIDBOILERPLATE_HID_H
