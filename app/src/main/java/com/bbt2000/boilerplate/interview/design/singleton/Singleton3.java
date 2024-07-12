package com.bbt2000.boilerplate.interview.design.singleton;

/**
 * 正确写法3：静态内部类
 */
class Singleton3 {
    private Singleton3() {
    }

    private static final class InstanceHolder {
        static final Singleton3 instance = new Singleton3();
    }

    public static Singleton3 getInstance() {
        return InstanceHolder.instance;
    }
}
