package com.lollipop.lpreference.item

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lollipop.lpreference.PreferenceConfig
import com.lollipop.lpreference.R
import com.lollipop.lpreference.info.PreferenceGroupInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于分组显示的Item
 */
class PreferenceGroup(viewGroup: ViewGroup): PreferenceItem<PreferenceGroupInfo>(
    createView(viewGroup, R.layout.item_preference_group)) {

    private val titleView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.titleView).apply {
            setTextColor(PreferenceConfig.groupColor)
        }
    }

    override fun bind(info: PreferenceGroupInfo) {
        if (info.type == PreferenceGroupInfo.Type.Start) {
            titleView.text = info.title
            titleView.visibility = View.VISIBLE
        } else {
            titleView.visibility = View.GONE
        }
    }

}