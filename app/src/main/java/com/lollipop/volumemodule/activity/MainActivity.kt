package com.lollipop.volumemodule.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lollipop.volumemodule.R
import com.lollipop.volumemodule.util.ProviderFactory
import kotlinx.android.synthetic.main.activity_main.*

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

        initView()

    }

    private fun initView() {
        val providerTypeArray = IntArray(15) { it }
        previewList.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        previewList.adapter = PreviewAdapter(providerTypeArray) {
            PreferencesActivity.start(this, it)
        }
    }

    private class PreviewAdapter(private val data: IntArray,
                                 private val openSetting: (type: Int) -> Unit)
        : RecyclerView.Adapter<PreviewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewHolder {
            return PreviewHolder.create(parent, viewType, openSetting)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return data[position]
        }

        override fun onBindViewHolder(holder: PreviewHolder, position: Int) {
            holder.onBind()
        }

    }

    private class PreviewHolder(view: View,
                                private val providerType: Int,
                                private val openSetting: (type: Int) -> Unit)
        : RecyclerView.ViewHolder(view) {

        companion object {
            fun create(group: ViewGroup, type: Int, openSetting: (type: Int) -> Unit): PreviewHolder {
                return PreviewHolder(LayoutInflater.from(group.context)
                    .inflate(R.layout.item_preview_provider, group, false),
                    type, openSetting)
            }
        }

        init {
            itemView.findViewById<View>(R.id.previewSettingBtn).setOnClickListener {
                openSetting(providerType)
            }
            itemView.findViewById<TextView>(R.id.previewTitleView)
                .setText(R.string.h2os)

            // TODO 设置预览图像
        }

        fun onBind() {

        }

    }

}
