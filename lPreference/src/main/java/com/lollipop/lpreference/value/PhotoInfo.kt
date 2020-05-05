package com.lollipop.lpreference.value

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * @author lollipop
 * @date 2020-02-12 21:49
 * 图片信息
 */
class PhotoInfo(val path: Uri, val size: Int, val name: String) {
    fun loadTo(view: ImageView) {
        Glide.with(view).load(path).into(view)
    }
}