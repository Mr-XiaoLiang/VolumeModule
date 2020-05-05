package com.lollipop.lpreference

import android.view.ViewGroup
import com.lollipop.lpreference.info.*
import com.lollipop.lpreference.item.*

/**
 * @author lollipop
 * @date 2020-01-18 20:35
 * 偏好设置工厂
 */
object PreferenceFactory {

    private const val Group  = -1
    private const val Empty  = 0
    private const val Number = 1
    private const val Action = 2
    private const val Switch = 3
    private const val Colors = 4
    private const val Images = 5

    fun getInfoType(info: PreferenceInfo): Int {
        return when (info) {
            is NumberPreferenceInfo -> Number
            is ActionPreferenceInfo -> Action
            is SwitchPreferenceInfo -> Switch
            is ColorsPreferenceInfo -> Colors
            is ImagesPreferenceInfo -> Images
            is PreferenceGroupInfo -> Group
            else -> Empty
        }
    }

    fun createItem(group: ViewGroup, type: Int): PreferenceItem<*> {
        return when(type) {
            Number -> NumberPreference(group)
            Action -> ActionPreference(group)
            Switch -> SwitchPreference(group)
            Colors -> ColorsPreference(group)
            Images -> ImagesPreference(group)
            Group -> PreferenceGroup(group)
            else -> EmptyPreferenceItem(group)
        }
    }

    fun bindItem(item: PreferenceItem<*>, info: PreferenceInfo) {
        when (item) {
            is NumberPreference -> if (info is NumberPreferenceInfo) {
                item.bind(info)
            }
            is ActionPreference -> if (info is ActionPreferenceInfo) {
                item.bind(info)
            }
            is SwitchPreference -> if (info is SwitchPreferenceInfo) {
                item.bind(info)
            }
            is ColorsPreference -> if (info is ColorsPreferenceInfo) {
                item.bind(info)
            }
            is ImagesPreference -> if (info is ImagesPreferenceInfo) {
                item.bind(info)
            }
            is PreferenceGroup -> if (info is PreferenceGroupInfo) {
                item.bind(info)
            }
            is EmptyPreferenceItem -> if (info is BasePreferenceInfo<*>) {
                item.bind(info)
            }
        }
    }

}