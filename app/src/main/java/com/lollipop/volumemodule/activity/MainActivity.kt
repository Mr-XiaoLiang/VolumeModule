package com.lollipop.volumemodule.activity

import android.content.Intent
import android.os.Bundle
import com.lollipop.volumemodule.R

/**
 * 主页的Activity
 */
class MainActivity : BaseActivity() {

    companion object {
        private const val ACTION_BLACK_LIST = 200
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showActionBtn(R.drawable.ic_assignment_black_24dp, ACTION_BLACK_LIST) {
            startActivity(Intent(this, BlackListActivity::class.java))
        }
    }

}
