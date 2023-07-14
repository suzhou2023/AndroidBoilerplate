//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_NATIVERENDERER_H
#define ANDROIDBOILERPLATE_NATIVERENDERER_H


#include "BaseArt.h"


class NativeRenderer {
    NativeRenderer();

    ~NativeRenderer();

public:
    static NativeRenderer *getInstance() {
        static NativeRenderer instance;
        return &instance;
    }

    void onSurfaceCreated();

    void onSurfaceChanged(int width, int height);

    void onDrawFrame();

private:
    BaseArt *m_pArt{};
    int m_ScreenW{};
    int m_ScreenH{};
};


#endif //ANDROIDBOILERPLATE_NATIVERENDERER_H
