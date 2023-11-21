package com.bbt2000.boilerplate.common.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

/**
 *  author : sz
 *  date : 2023/9/25
 *  description :
 */
object StatusBarUtil {
    // 获取状态栏高度dp
    fun getStatusBarHeightDp(): Float {
        val context = ContextUtil.application
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            val px = context.resources.getDimensionPixelSize(resourceId)
            px / context.resources.displayMetrics.density
        } else { // 基本不会发生这种情况
            32f
        }
    }

    // 获取导航栏高度dp
    fun getNavigationBarHeightDp(): Float {
        val context = ContextUtil.application
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            val px = context.resources.getDimensionPixelSize(resourceId)
            px / context.resources.displayMetrics.density
        } else { // 基本不会发生这种情况
            0f
        }
    }

    // 状态栏透明
    fun transparentStatusBar(window: Window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility =
            systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.decorView.systemUiVisibility = systemUiVisibility
        window.statusBarColor = Color.TRANSPARENT
    }

    // 状态栏文字颜色
    fun setStatusBarTextColor(window: Window, light: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = if (light) { //白色文字
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else { //黑色文字
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            window.decorView.systemUiVisibility = systemUiVisibility
        }
    }

    // 导航栏透明
    fun transparentNavigationBar(window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        // 8.0以下不去设置导航栏透明了，兼容起来很麻烦
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility =
                systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.decorView.systemUiVisibility = systemUiVisibility
            window.navigationBarColor = Color.TRANSPARENT
        }
    }

    // 导航栏按钮或导航条颜色(8.0以上支持)
    fun setNavigationBarBtnColor(window: Window, light: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var systemUiVisibility = window.decorView.systemUiVisibility
            systemUiVisibility = if (light) { //白色按钮
                systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
            } else { //黑色按钮
                systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
            window.decorView.systemUiVisibility = systemUiVisibility
        }
    }
}

























