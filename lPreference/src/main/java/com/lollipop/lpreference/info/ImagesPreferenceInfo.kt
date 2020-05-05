package com.lollipop.lpreference.info

import android.content.Context
import android.net.Uri
import com.lollipop.lpreference.value.ColorArray
import com.lollipop.lpreference.value.ImageArray

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 颜色偏好设置
 */
class ImagesPreferenceInfo(key: String): BasePreferenceInfo<ImageArray>(key, ImageArray()) {


    var maxSize = -1

    fun save(context: Context) {
        putValue(context, default)
    }

    fun onImageChange(value: Array<Uri>) {
        values.clear()
        values.addAll(*value)
    }

    val values: ImageArray
        get() {
            return default
        }

}