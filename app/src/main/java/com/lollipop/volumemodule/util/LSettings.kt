package com.lollipop.volumemodule.util

import android.content.Context
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

}