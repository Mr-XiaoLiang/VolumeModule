package com.lollipop.volumemodule.volume

/**
 * @author lollipop
 * @date 2020/5/5 21:16
 * 音量调节接口
 */
interface VolumeProvider {
    fun onVolumeChanged(type: StreamType)
}