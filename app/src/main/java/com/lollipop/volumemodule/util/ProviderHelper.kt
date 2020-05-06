package com.lollipop.volumemodule.util

import android.content.Context
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import com.lollipop.volumemodule.R
import com.lollipop.volumemodule.provider.BaseProvider
import com.lollipop.volumemodule.provider.ProviderController
import com.lollipop.volumemodule.volume.StreamType
import com.lollipop.volumemodule.volume.VolumeController
import com.lollipop.volumemodule.volume.VolumeProvider

/**
 * @author lollipop
 * @date 2020/5/5 22:11
 * 呈现控制器
 */
class ProviderHelper(private val context: Context,
                     private val controller: VolumeController,
                     private val providerType: (() -> Int) = { LSettings.defaultProviderType(context) },
                     private val providerGroup: ProviderGroup = FloatingManager(context))
    : VolumeProvider,
    ProviderController {

    private var provider: BaseProvider? = null
    private var needUpdateLayout = false

    init {
        val emptyGroup = FrameLayout(context)
        val view = LayoutInflater.from(context).inflate(R.layout.provider_h2os, emptyGroup, false)
        view.findViewById<View>(R.id.changeBtn).setOnClickListener {
            view.findViewById<View>(R.id.valueView).let {
                it.visibility = if (it.visibility == View.VISIBLE) { View.GONE } else { View.VISIBLE }
            }
        }
        val srcParams = view.layoutParams
        val p = providerGroup.createParams() as WindowManager.LayoutParams
        p.gravity = Gravity.LEFT
        p.width = srcParams.width
        p.height = srcParams.height
        providerGroup.addView(view, p)
    }

    override fun onVolumeChanged(type: StreamType) {
//        checkProvider()
//        provider?.onVolumeChanged(type)
    }

    interface ProviderGroup: ViewManager {
        fun createParams(): ViewGroup.LayoutParams
    }

    private class FloatingManager(context: Context): ProviderGroup {

        private val windowManager: WindowManager by lazy {
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }

        override fun createParams(): ViewGroup.LayoutParams {
            return FloatingViewHelper.createParams()
        }

        override fun addView(view: View?, params: ViewGroup.LayoutParams?) {
            windowManager.addView(view, params)
        }

        override fun updateViewLayout(view: View?, params: ViewGroup.LayoutParams?) {
            windowManager.updateViewLayout(view, params)
        }

        override fun removeView(view: View?) {
            windowManager.removeView(view)
        }

    }

    /**
     * 当OSD需要重新布局时，立刻发起一次新的布局，
     * 由于是主动发起，因此立即执行
     */
    override fun callUpdateLayout() {
        needUpdateLayout = true
        checkProvider()
    }

    /**
     * 当OSD被关闭时，记录一次更新状态，下次打开时使用新的布局
     */
    override fun onDismiss() {
        needUpdateLayout = true
    }

    private fun checkProvider() {
        // 获取当前需要操作的Provider类型
        val pType = providerType()
        // 当前已有Provider
        val oldProvider = provider
        // 找不到呈现器或者类型不符的时候，创建新的呈现器
        val targetProvider = if (oldProvider == null || ProviderFactory.getType(oldProvider) != pType) {
            ProviderFactory.createProvider(pType, context,controller, this)
        } else {
            oldProvider
        }
        if (targetProvider != oldProvider) {
            // 如果两个Provider不一致，那么移除旧的
            oldProvider?.let {
                // 移除并且做必要的清理操作
                providerGroup.removeView(it.providerView)
                it.destroy()
            }
            // 添加新的Provider
            val layoutParams = FloatingViewHelper.createParams()
            targetProvider.updateLayout(layoutParams)
            providerGroup.addView(targetProvider.providerView, layoutParams)
        } else if (needUpdateLayout) {
            // 如果Provider没有发生变化，但是如果有必要仍然发起一次更新操作
            val layoutParams = providerGroup.createParams()
            targetProvider.updateLayout(layoutParams)
            providerGroup.updateViewLayout(targetProvider.providerView, layoutParams)
        }
        needUpdateLayout = false
    }

}