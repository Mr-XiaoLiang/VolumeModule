package com.lollipop.lpreference.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.info.PreferenceInfo

/**
 * @author lollipop
 * @date 2020-02-16 19:27
 * 偏好设置的item的接口
 */
abstract class PreferenceItem<T: PreferenceInfo>(view: View): RecyclerView.ViewHolder(view) {

    companion object {
        fun createView(viewGroup: ViewGroup, id: Int): View {
            return LayoutInflater.from(viewGroup.context).inflate(
                id, viewGroup, false)
        }
    }

    abstract fun bind(info: T)

}