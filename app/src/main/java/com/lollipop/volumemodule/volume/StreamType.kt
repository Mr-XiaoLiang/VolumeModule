package com.lollipop.volumemodule.volume

import android.media.AudioManager

/**
 * @author lollipop
 * @date 2020/5/5 21:25
 * 音频流的类型
 */
enum class StreamType(val value: Int) {

    /**
     * 语音通话声音
     */
    VOICE_CALL(AudioManager.STREAM_VOICE_CALL),

    /**
     * 系统提示音
     */
    SYSTEM(AudioManager.STREAM_SYSTEM),

    /**
     * 来电铃声
     */
    RING(AudioManager.STREAM_RING),

    /**
     * 媒体音频
     */
    MUSIC(AudioManager.STREAM_MUSIC),

    /**
     * 警报提示音
     */
    ALARM(AudioManager.STREAM_ALARM),

    /**
     * 通知提示音
     */
    NOTIFICATION(AudioManager.STREAM_NOTIFICATION),

    /**
     * DTMF音频
     */
    DTMF(AudioManager.STREAM_DTMF)

}