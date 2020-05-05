package com.lollipop.lpreference.info

import android.content.Context
import com.lollipop.lpreference.value.ColorArray

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 颜色偏好设置
 */
class ColorsPreferenceInfo(key: String):
    BasePreferenceInfo<ColorArray>(key, ColorArray()) {

    init {
        onValueGetter = {
            if (it.size < 1) {
                it.addAll(*defaultColors)
            }
            if (maxSize > 0) {
                while (it.size > maxSize) {
                    it.removeAt(0)
                }
            }
            it
        }
    }

    var maxSize = -1
    var defaultColors = IntArray(0)

    fun onColorChange(colors: IntArray) {
        default.clear()
        default.addAll(*colors)
    }

    fun save(context: Context) {
        putValue(context, default)
    }

}