package com.lollipop.lpreference.info

import android.content.Context
import com.lollipop.lpreference.PreferenceHelper
import com.lollipop.lpreference.value.PreferenceValue

/**
 * @author lollipop
 * @date 2020-01-16 23:43
 * 基础的偏好设置信息
 */
open class BasePreferenceInfo<T: Any>(val key: String, val default: T): PreferenceInfo {
    /**
     * 标题
     */
    var title: CharSequence = ""

    /**
     * 描述
     */
    var summary: CharSequence = ""

    /**
     * icon的图标ID
     */
    var iconId: Int = 0

    /**
     * 是否启用
     */
    var enable = true

    /**
     * 关注的对象
     */
    var relevantKey = ""
    /**
     * 关注的结果
     */
    var relevantEnable = true

    protected var lastValue: T = default

    protected var onValueGetter: ((T) -> T)? = null

    @Suppress("UNCHECKED_CAST")
    fun getValue(context: Context): T {
        val result = when (default) {
            is PreferenceValue -> {
                PreferenceHelper.get(context, key, default)
            }
            is String -> {
                PreferenceHelper.get(context, key, default)
            }
            is Int -> {
                PreferenceHelper.get(context, key, default)
            }
            is Boolean -> {
                PreferenceHelper.get(context, key, default)
            }
            is Float -> {
                PreferenceHelper.get(context, key, default)
            }
            is Double -> {
                PreferenceHelper.get(context, key, default)
            }
            is Long -> {
                PreferenceHelper.get(context, key, default)
            }
            else -> {
                throw RuntimeException("Unknown value type, " +
                        "please Implementing the PreferenceValue interface")
            }
        } as T
        lastValue = result
        return onValueGetter?.invoke(result)?:result
    }

    fun putValue(context: Context, value: T) {
        lastValue = value
        when (value) {
            is PreferenceValue -> {
                PreferenceHelper.put(context, key, value)
            }
            is String -> {
                PreferenceHelper.put(context, key, value)
            }
            is Int -> {
                PreferenceHelper.put(context, key, value)
            }
            is Boolean -> {
                PreferenceHelper.put(context, key, value)
            }
            is Float -> {
                PreferenceHelper.put(context, key, value)
            }
            is Double -> {
                PreferenceHelper.put(context, key, value)
            }
            is Long -> {
                PreferenceHelper.put(context, key, value)
            }
            else -> {
                throw RuntimeException("Unknown value type, " +
                        "please Implementing the PreferenceValue interface")
            }
        }
    }

}