package com.lollipop.lpreference.view

import android.content.Intent
import com.lollipop.lpreference.info.*
import com.lollipop.lpreference.value.ColorArray

/**
 * @author lollipop
 * @date 2020-02-16 19:55
 * 偏好设置的列表的builder
 */
class ItemBuilder {

    val itemList = ArrayList<PreferenceInfo>()

    fun number(key: String, def: Int = 0, run: NumberPreferenceInfo.() -> Unit): NumberPreferenceInfo {
        return NumberPreferenceInfo(key, def).apply(run)
    }

    fun action(action: Intent,
               run: ActionPreferenceInfo.() -> Unit): ActionPreferenceInfo {
        return ActionPreferenceInfo(action).apply(run)
    }

    fun switch(key: String, def: Boolean = false, run: SwitchPreferenceInfo.() -> Unit): SwitchPreferenceInfo {
        return SwitchPreferenceInfo(key, def).apply(run)
    }

    fun colors(key: String, run: ColorsPreferenceInfo.() -> Unit): ColorsPreferenceInfo {
        return ColorsPreferenceInfo(key).apply(run)
    }

    fun images(key: String, run: ImagesPreferenceInfo.() -> Unit): ImagesPreferenceInfo {
        return ImagesPreferenceInfo(key).apply(run)
    }

    fun group(title: CharSequence, run: ItemBuilder.() -> Unit): TempGroupInfo {
        val tempGroupInfo = TempGroupInfo(title)
        val builder = ItemBuilder()
        run.invoke(builder)
        tempGroupInfo.items.addAll(builder.itemList)
        return tempGroupInfo
    }

    fun add(vararg infoArray: PreferenceInfo) {
        for (info in infoArray) {
            if (info is TempGroupInfo) {
                itemList.add(PreferenceGroupInfo(info.title, PreferenceGroupInfo.Type.Start))
                for (child in info.items) {
                    if (child is PreferenceGroupInfo || child is TempGroupInfo) {
                        continue
                    }
                    itemList.add(child)
                }
                itemList.add(PreferenceGroupInfo("", PreferenceGroupInfo.Type.End))
            } else {
                itemList.add(info)
            }
        }
    }

    class TempGroupInfo(val title: CharSequence): PreferenceInfo {
        val items = ArrayList<PreferenceInfo>()
    }

}