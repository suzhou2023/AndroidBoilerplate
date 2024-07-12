package com.bbt2000.boilerplate.interview.design.singleton

import java.util.concurrent.Executors

/**
 * author : suzhou
 * date : 2024/7/11 07:38
 * description :
 */
internal object Test {
    @JvmStatic
    fun main(args: Array<String>) {
        val service = Executors.newFixedThreadPool(20)
        for (i in 0..19) {
            service.execute {
                val instance = Singleton1.getInstance()
                println(instance)
            }
        }
    }
}
