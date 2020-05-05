package com.lollipop.lpreference.value

/**
 * @author lollipop
 * @date 2020-01-18 20:55
 * 偏好设置的值的接口
 */
interface PreferenceValue {

    fun serialization(): String
    fun parse(value: String)
}