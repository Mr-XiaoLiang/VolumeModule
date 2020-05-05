package com.lollipop.volumemodule.activity

import android.app.Activity
import android.content.Intent
import com.lollipop.volumemodule.R

/**
 * 偏好设置
 */
class PreferencesActivity : BaseActivity() {

    companion object {

        private const val ARG_TYPE_ID = "ARG_TYPE_ID"

        fun start(context: Activity, typeId: Int) {
            context.startActivityForResult(Intent(context, PreferencesActivity::class.java).apply {
                putExtra(ARG_TYPE_ID, typeId)
            }, typeId)
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_preferences
}
