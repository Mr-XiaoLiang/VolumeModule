package com.lollipop.volumemodule.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.lollipop.volumemodule.util.ProviderHelper
import com.lollipop.volumemodule.volume.AdjustType
import com.lollipop.volumemodule.volume.StreamType
import com.lollipop.volumemodule.volume.VolumeController

/**
 * 音量调节服务
 */
class VolumeAccessibilityService : AccessibilityService(), VolumeController {

    /**
     * 媒体管理器
     */
    private val audioManager: AudioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * 视觉呈现管理器
     */
    private val provider: ProviderHelper by lazy {
        ProviderHelper(this, this)
    }

    override fun onInterrupt() {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
    }

    /**
     * 按键的监听
     * 主要事件来源
     */
    override fun onKeyEvent(event: KeyEvent?): Boolean {
        when (event?.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                val type = defaultStreamType()
                changeVolume(type, AdjustType.RAISE)
                provider.onVolumeChanged(type)
                return true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                val type = defaultStreamType()
                changeVolume(type, AdjustType.LOWER)
                provider.onVolumeChanged(type)
                return true
            }
        }
        return super.onKeyEvent(event)
    }

    /**
     * 获取当前默认的流类型
     */
    private fun defaultStreamType(): StreamType {
        return StreamType.MUSIC
    }

    override fun getMaxVolume(type: StreamType): Int {
        return audioManager.getStreamMaxVolume(type.value)
    }

    override fun getMinVolume(type: StreamType): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return audioManager.getStreamMinVolume(type.value)
        }
        return 0
    }

    override fun getCurrentVolume(type: StreamType): Int {
        return audioManager.getStreamVolume(type.value)
    }

    override fun changeVolume(type: StreamType, adjustType: AdjustType) {
        audioManager.adjustStreamVolume(type.value, adjustType.value, 0)
    }

    override fun getDefaultType(): StreamType {
        return defaultStreamType()
    }

}
