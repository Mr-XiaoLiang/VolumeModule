package com.lollipop.volumemodule.provider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.lollipop.lpreference.view.ItemBuilder
import com.lollipop.volumemodule.volume.VolumeController
import com.lollipop.volumemodule.volume.VolumeProvider

/**
 * @author lollipop
 * @date 2020/5/5 23:52
 * 基础的呈现器，用于提供标准化接口
 */
abstract class BaseProvider(protected val context: Context,
                            protected val controller: VolumeController,
                            private val providerController: ProviderController): VolumeProvider {

    val providerView: View by lazy {
        createView()
    }

    abstract val providerName: Int

    val preferencesBuilder: (ItemBuilder.() -> Unit) by lazy {
        createPreferencesBuilder()
    }

    protected abstract fun createView(): View

    protected fun createViewById(id: Int): View {
        val emptyGroup = FrameLayout(context)
        return LayoutInflater.from(context).inflate(id, emptyGroup, false)
    }

    open fun destroy() {

    }

    open fun updateLayout(layoutParams: ViewGroup.LayoutParams) {
        providerView.layoutParams?.let {
            layoutParams.width = it.width
            layoutParams.height = it.height
        }
    }

    protected fun trySetGravity(layoutParams: ViewGroup.LayoutParams, gravity: Int) {
        when (layoutParams) {
            is WindowManager.LayoutParams -> {
                layoutParams.gravity = gravity
            }
            is FrameLayout.LayoutParams -> {
                layoutParams.gravity = gravity
            }
            is LinearLayout.LayoutParams -> {
                layoutParams.gravity = gravity
            }
            is CoordinatorLayout.LayoutParams -> {
                layoutParams.gravity = gravity
            }
        }
    }

    protected fun callUpdateLocation() {
        providerController.callUpdateLayout()
    }

    protected open fun createPreferencesBuilder(): ItemBuilder.() -> Unit {
        return {}
    }

}