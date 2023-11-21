/**
 *  author : sz
 *  date : 2023/11/21
 *  description : 
 */


#ifndef ANDROIDBOILERPLATE_VERTICES_H
#define ANDROIDBOILERPLATE_VERTICES_H


float vertices[] = {
        // 顶点坐标                        颜色
        -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,//背后左下角点 0
        0.5f, -0.5f, -0.5f, 0.0f, 1.0f, 0.0f,//背后右下角点 1

        0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 1.0f,//背后右上角点 2
        -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, 0.0f,//背后左上角点 3

        -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 0.0f,//前面左下角点 4
        0.5f, -0.5f, 0.5f, 0.0f, 1.0f, 1.0f,//前面右下角点 5

        0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,//前面右上角点 6
        -0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,//前面左上角点 7
};

unsigned int indices[] = {
        //背面
        0, 3, 1, // first triangle
        3, 2, 1, // second triangle
        //上面
        2, 3, 7, // first triangle
        7, 6, 2,  // second triangle
        //左面
        3, 0, 4, // first triangle
        4, 7, 3, // second triangle
        //右面
        5, 1, 2, // first triangle
        2, 6, 5, // second triangle
        //下面
        4, 0, 1, // first triangle
        1, 5, 4,// second triangle
        //前面
        4, 5, 6, // first triangle
        6, 7, 4, // second triangle
};


#endif //ANDROIDBOILERPLATE_VERTICES_H
