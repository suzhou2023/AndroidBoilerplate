package com.bbt2000.boilerplate.interview.design.singleton;

/**
 * 问题写法1：多线程环境有可能构造出多个实例
 */
class SingletonErr1 {
    private static SingletonErr1 instance;

    private SingletonErr1() {
    }

    public static SingletonErr1 getInstance() {
        if (instance == null) {
            instance = new SingletonErr1();
        }
        return instance;
    }
}
