package com.bbt2000.boilerplate.interview.design.singleton;

/**
 * 正确写法2：双重检验锁
 */
class Singleton2 {
    private static Singleton2 instance;

    private Singleton2() {
    }

    public static Singleton2 getInstance() {
        if (instance == null) {
            synchronized (Singleton2.class) {
                if (instance == null) {
                    instance = new Singleton2();
                }
            }
        }
        return instance;
    }
}
