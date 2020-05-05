package com.lollipop.volumemodule.provider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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

    val preferencesBuilder: (ItemBuilder.() -> Unit) by lazy {
        createPreferencesBuilder()
    }

    protected abstract fun createView(): View

    open fun destroy() {

    }

    open fun updateLayout(layoutParams: ViewGroup.LayoutParams) {

    }

    protected fun callUpdateLocation() {
        providerController.callUpdateLayout()
    }

    protected open fun createPreferencesBuilder(): ItemBuilder.() -> Unit {
        return {}
    }

}