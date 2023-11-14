package com.bbt2000.boilerplate.common.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity


/**
 *  author : sz
 *  date : 2023/7/11 14:00
 *  description :
 */

object ContextUtil {
    lateinit var application: Application

    fun getActivity(context: Context): Activity {
        var curContext = context
        while (curContext is ContextWrapper) {
            if (curContext is Activity) {
                return curContext
            }
            curContext = curContext.baseContext
        }
        throw RuntimeException("Cannot find Activity")
    }

    fun getAppCompatActivity(context: Context): AppCompatActivity {
        var curContext = context
        while (curContext is ContextWrapper) {
            if (curContext is AppCompatActivity) {
                return curContext
            }
            curContext = curContext.baseContext
        }
        throw RuntimeException("Cannot find AppCompatActivity")
    }
}
















