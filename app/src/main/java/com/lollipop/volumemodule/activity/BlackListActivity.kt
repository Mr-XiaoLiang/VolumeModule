package com.lollipop.volumemodule.activity

import android.os.Bundle
import android.view.View
import com.lollipop.volumemodule.R
import kotlinx.android.synthetic.main.activity_black_list.*

/**
 * 黑名单的页面
 */
class BlackListActivity : BaseActivity() {
    override val layoutId: Int
        get() = R.layout.activity_black_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeBtn.setOnClickListener {
            valueView.visibility = if (valueView.visibility == View.VISIBLE) { View.GONE } else { View.VISIBLE }
        }
    }

}
