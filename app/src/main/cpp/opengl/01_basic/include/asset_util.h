/**
 *  author : suzhou
 *  date : 2023/11/13 
 *  description : 
 */




#ifndef ANDROIDBOILERPLATE_ASSET_UTIL_H
#define ANDROIDBOILERPLATE_ASSET_UTIL_H


#include <GLES3/gl3.h>
#include <android/asset_manager_jni.h>


// 返回字符数组，用完后需要手动调用delete释放空间
GLubyte *readFile(AAssetManager *assetManager, const char *filename);


#endif //ANDROIDBOILERPLATE_ASSET_UTIL_H
