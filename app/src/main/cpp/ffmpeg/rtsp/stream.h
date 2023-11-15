/**
 *  author : suzhou
 *  date : 2023/10/21 
 *  description : 
 */




#ifndef BBT_COLPOSCOPE_PATIENT_STREAM_H
#define BBT_COLPOSCOPE_PATIENT_STREAM_H

#include "gl/GLContext.h"

extern "C" {
void openRtspStream(GLContext *glContext, const char *url);
}


#endif //BBT_COLPOSCOPE_PATIENT_STREAM_H
