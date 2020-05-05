package com.lollipop.lpreference

import android.text.TextUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.info.BasePreferenceInfo
import com.lollipop.lpreference.info.PreferenceInfo
import com.lollipop.lpreference.item.BasePreferenceItem
import com.lollipop.lpreference.item.PreferenceItem
import com.lollipop.lpreference.item.StatusProvider

/**
 * @author lollipop
 * @date 2020-01-18 20:32
 * 偏好设置的适配器
 */
class PreferenceAdapter(private val data: ArrayList<PreferenceInfo>):
    RecyclerView.Adapter<PreferenceItem<*>>() {

    var onPreferenceChangeListener: ((BasePreferenceInfo<*>) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceItem<*> {
        val item = PreferenceFactory.createItem(parent, viewType)
        if (item is BasePreferenceItem) {
            item.init(
                {
                    val info = data[it] as BasePreferenceInfo<*>
                    getStatus(info)
                },
                {
                    val info = data[it] as BasePreferenceInfo<*>
                    onPreferenceChangeListener?.invoke(info)
                    checkItemStatus(info)
                }
            )
        }
        return item
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: PreferenceItem<*>, position: Int) {
        PreferenceFactory.bindItem(holder, data[position])
    }

    private fun getStatus(info: BasePreferenceInfo<*>): Boolean {
        if (TextUtils.isEmpty(info.relevantKey)) {
            return info.enable
        }
        val relevantInfo = PreferenceHelper.findItemByKey(info.relevantKey, data) ?: return info.enable
        return if (relevantInfo is StatusProvider) {
            relevantInfo.statusValue == info.relevantEnable
        } else {
            info.enable
        }
    }

    private fun checkItemStatus(info: BasePreferenceInfo<*>) {
        if (TextUtils.isEmpty(info.key)) {
            return
        }
        if (info !is StatusProvider) {
            return
        }
        val statusValue = info.statusValue
        for (index in data.indices) {
            val item = data[index]
            if (item == info || item !is BasePreferenceInfo<*>) {
                continue
            }
            if (item.relevantKey == info.key) {
                item.enable = item.relevantEnable == statusValue
                notifyItemChanged(index)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return PreferenceFactory.getInfoType(data[position])
    }
}