package com.lollipop.lpreference.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lollipop.lpreference.PreferenceHelper
import com.lollipop.lpreference.R
import com.lollipop.lpreference.info.NumberPreferenceInfo
import com.lollipop.lpreference.dialog.CardWheelDialogFragment
import com.lollipop.lpreference.util.range

/**
 * @author lollipop
 * @date 2020-01-18 19:50
 * 数字设置的偏好设置项
 */
class NumberPreference(viewGroup: ViewGroup): BasePreferenceItem<NumberPreferenceInfo>(viewGroup) {

    override val widgetId: Int
        get() = R.layout.preference_plugin_number

    private var number = 0

    private val numberView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.numberView)
    }

    private val alternative: ArrayList<NumberInfo> by lazy {
        ArrayList<NumberInfo>(10).apply {
            for (i in 0..9) {
                add(NumberInfo("$i", i))
            }
        }
    }

    override fun onItemClick(view: View) {
        super.onItemClick(view)
        CardWheelDialogFragment.show(context, alternative, number,
            "NumberPreference", NumberSelectedCallback(context, preferenceKey) { key ->
                if (key == preferenceKey) {
                    notifyInfoChange()
                }
            })
    }

    override fun onBind(info: NumberPreferenceInfo) {
        super.onBind(info)
        number = info.getValue(context)
        numberView.text = number.range(0, 9).toString()
    }

    override fun onStatusChange(isEnable: Boolean) {
        super.onStatusChange(isEnable)
        numberView.setStatus(isEnable)
    }

    private class NumberInfo(name: String, val value: Int): CardWheelDialogFragment.CardInfo(name)

    private class NumberSelectedCallback(private val context: Context,
                                         private val key: String,
                                         private val onChanged: (key: String) -> Unit): CardWheelDialogFragment.OnSelectedListener {
        override fun onCardSelected(info: CardWheelDialogFragment.CardInfo) {
            if (info is NumberInfo) {
                PreferenceHelper.put(context, key, info.value)
                onChanged(key)
            }
        }
    }

}