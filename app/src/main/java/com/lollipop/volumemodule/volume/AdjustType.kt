package com.lollipop.volumemodule.volume

import android.media.AudioManager

/**
 * @author lollipop
 * @date 2020/5/5 21:45
 * 调整方式
 */
enum class AdjustType(val value: Int) {
    /**
     * 增加音量
     */
    RAISE(AudioManager.ADJUST_RAISE),

    /**
     * 降低音量
     */
    LOWER(AudioManager.ADJUST_LOWER),

    /**
     * 保持不变
     * 此功能主要用于触发OSD的展示
     */
    SAME(AudioManager.ADJUST_SAME),

    /**
     * 静音
     * 可能存在版本限制
     */
    MUTE(AudioManager.ADJUST_MUTE),

    /**
     * 取消静音
     */
    UNMUTE(AudioManager.ADJUST_UNMUTE),

    /**
     * 切换静音状态。 如果静音，则流将取消静音。 如果未静音，则流将被静音。
     */
    TOGGLE_MUTE(AudioManager.ADJUST_TOGGLE_MUTE)
}