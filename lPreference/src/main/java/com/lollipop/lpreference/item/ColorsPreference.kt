package com.lollipop.lpreference.item

import android.view.View
import android.view.ViewGroup
import com.lollipop.lpreference.R
import com.lollipop.lpreference.dialog.ColorsPanelDialogFragment
import com.lollipop.lpreference.info.ColorsPreferenceInfo
import com.lollipop.lpreference.view.ColorWheelView

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个用于颜色的偏好设置项
 */
class ColorsPreference(group: ViewGroup): BasePreferenceItem<ColorsPreferenceInfo>(group) {
    override val widgetId: Int = R.layout.preference_plugin_colors

    private val colorsView: ColorWheelView by lazy {
        itemView.findViewById<ColorWheelView>(R.id.colorsView)
    }

    override fun onItemClick(view: View) {
        super.onItemClick(view)
        preferenceInfo?.let { info ->
            ColorsPanelDialogFragment.show(context, info.getValue(context), info.maxSize) {
                info.onColorChange(it)
                info.save(context)
                notifyInfoChange()
            }
        }
    }

    override fun onPreviewClick(view: View): Boolean {
        super.onPreviewClick(view)
        if (preferenceInfo?.maxSize?:1 == 1) {
            return false
        }
        colorsView.isGradient = !colorsView.isGradient
        return true
    }

    override fun onBind(info: ColorsPreferenceInfo) {
        super.onBind(info)
        colorsView.setColor(info.getValue(context))
    }

}