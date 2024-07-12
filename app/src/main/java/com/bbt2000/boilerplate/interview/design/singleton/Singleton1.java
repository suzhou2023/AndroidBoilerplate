package com.bbt2000.boilerplate.interview.design.singleton;

/**
 * 正确写法1
 */
class Singleton1 {
    private static Singleton1 instance;

    private Singleton1() {
    }

    public static synchronized Singleton1 getInstance() {
        if (instance == null) {
            instance = new Singleton1();
        }
        return instance;
    }
}
