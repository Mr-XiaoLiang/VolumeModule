package com.lollipop.volumemodule.util

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.lollipop.lpreference.PreferenceHelper


/**
 * @author lollipop
 * @date 2020/5/6 00:48
 * 偏好设置中心
 */
object LSettings {

    private const val KEY_DEFAULT_PROVIDER = "KEY_DEFAULT_PROVIDER"

    fun defaultProviderType(context: Context): Int {
        return PreferenceHelper.get(context, KEY_DEFAULT_PROVIDER, ProviderFactory.DEFAULT_TYPE)
    }

    fun isAccessibilityServiceEnable(context: Context): Boolean {
        val accessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val accessibilityServices =
            accessibilityManager.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (info in accessibilityServices) {
            if (info.id.contains(context.packageName)) {
                return true
            }
        }
        return false
    }

}