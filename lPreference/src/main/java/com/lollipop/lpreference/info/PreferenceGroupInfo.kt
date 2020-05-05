package com.lollipop.lpreference.info

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 偏好设置的分组标识
 */
class PreferenceGroupInfo(val title: CharSequence, val type: Type): PreferenceInfo {
    enum class Type {
        Start, End
    }
}