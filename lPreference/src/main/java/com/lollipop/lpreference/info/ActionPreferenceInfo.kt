package com.lollipop.lpreference.info

import android.content.Intent
import java.util.*

/**
 * @author lollipop
 * @date 2020-01-18 20:38
 * 跳转意图偏好设置
 */
class ActionPreferenceInfo(val action: Intent): BasePreferenceInfo<Intent>(
    randomKey(), Intent()) {

    companion object {
        private
        fun randomKey(): String {
            val random = Random()
            return System.currentTimeMillis().toString(16) +
                    random.nextInt(256).toString(16)
        }
    }

    var actionType = ActionType.Activity

    enum class ActionType {
        Activity,
        Service,
        Broadcast
    }

}