//
// Created by sz on 2023/7/13.
//

#ifndef ANDROIDBOILERPLATE_01_TRIANGLE_H
#define ANDROIDBOILERPLATE_01_TRIANGLE_H


#include <GLES2/gl2.h>
#include "BaseArt.h"


class Triangle : public BaseArt {
public:
    Triangle();

    ~Triangle();

    void init();

    void draw(int screenW, int screenH);

    void destroy();
};

#endif //ANDROIDBOILERPLATE_01_TRIANGLE_H
