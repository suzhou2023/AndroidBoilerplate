package com.bbt2000.boilerplate.leak

import android.app.Activity

/**
 *  author : suzhou
 *  date : 2024/4/3 13:23
 *  description :
 */
object Leak {
    var activityLeak: Activity? = null
    var innerClass: Any? = null
    var staticInnerClass: Any? = null
}
