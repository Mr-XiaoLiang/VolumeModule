package com.lollipop.lpreference.item

import android.view.ViewGroup
import com.lollipop.lpreference.info.BasePreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-18 20:44
 * 一个空的偏好设置项
 */
class EmptyPreferenceItem(group: ViewGroup): BasePreferenceItem<BasePreferenceInfo<*>>(group) {
    override val widgetId: Int = 0
}