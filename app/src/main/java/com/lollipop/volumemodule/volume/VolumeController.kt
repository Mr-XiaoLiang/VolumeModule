package com.lollipop.volumemodule.volume

/**
 * @author lollipop
 * @date 2020/5/5 21:41
 */
interface VolumeController {

    fun getMaxVolume(type: StreamType): Int

    fun getMinVolume(type: StreamType): Int

    fun getCurrentVolume(type: StreamType): Int

    fun changeVolume(type: StreamType, adjustType: AdjustType)

}