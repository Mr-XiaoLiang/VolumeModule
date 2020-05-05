package com.lollipop.lpreference.item

import android.text.TextUtils
import android.view.ViewGroup
import android.widget.CompoundButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.lollipop.lpreference.R
import com.lollipop.lpreference.info.SwitchPreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于跳转的偏好设置项
 */
class SwitchPreference(group: ViewGroup): BasePreferenceItem<SwitchPreferenceInfo>(group),
    CompoundButton.OnCheckedChangeListener {

    override val widgetId: Int = R.layout.preference_plugin_switch

    private val switchView: SwitchMaterial by lazy {
        itemView.findViewById<SwitchMaterial>(R.id.switchView)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        preferenceInfo?.putValue(context, isChecked)
        notifyInfoChange()
    }

    override fun onBind(info: SwitchPreferenceInfo) {
        super.onBind(info)
        val value = info.getValue(context)
        if (!TextUtils.isEmpty(info.summaryTrue) || !TextUtils.isEmpty(info.summaryFalse)) {
            summary = if (value) { info.summaryTrue }  else { info.summaryFalse }
        }
        changeSwitch(value)
    }

    override fun onStatusChange(isEnable: Boolean) {
        super.onStatusChange(isEnable)
        switchView.isEnabled = isEnable
    }

    private fun changeSwitch(isChecked: Boolean) {
        switchView.setOnCheckedChangeListener(null)
        switchView.isChecked = isChecked
        switchView.setOnCheckedChangeListener(this)
    }

}