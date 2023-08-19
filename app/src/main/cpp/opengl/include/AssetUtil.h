/**
 *  author : suzhou
 *  date : 2023/8/19 
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_ASSETUTIL_H
#define ANDROIDBOILERPLATE_ASSETUTIL_H

#include <android/asset_manager_jni.h>


class AssetUtil {
public:
    /**
     * 返回字符数组，用完后需要手动调用delete释放空间
     *
     * @param assetManager
     * @param filename
     * @return
     */
    char *readFile(AAssetManager *assetManager, const char *filename) {
        AAsset *aAsset = AAssetManager_open(assetManager, filename, AASSET_MODE_BUFFER);
        off_t len = AAsset_getLength(aAsset);
        char *buf = new char[len + 1];
        buf[len] = '\0';
        AAsset_read(aAsset, buf, len);
        return buf;
    }
};


static AssetUtil assetUtil = AssetUtil();


#endif //ANDROIDBOILERPLATE_ASSETUTIL_H
