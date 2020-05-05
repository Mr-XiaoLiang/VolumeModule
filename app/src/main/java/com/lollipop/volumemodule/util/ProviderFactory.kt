package com.lollipop.volumemodule.util

import android.content.Context
import com.lollipop.volumemodule.R
import com.lollipop.volumemodule.provider.BaseProvider
import com.lollipop.volumemodule.provider.ProviderController
import com.lollipop.volumemodule.volume.VolumeController

/**
 * @author lollipop
 * @date 2020/5/6 00:49
 * 呈现器工厂
 */
object ProviderFactory {

    const val DEFAULT_TYPE = 0

    fun getType(provider: BaseProvider): Int {
        // TODO
        return DEFAULT_TYPE
    }

    fun createProvider(type: Int,
                       context: Context,
                       controller: VolumeController,
                       providerController: ProviderController): BaseProvider {
        TODO()
    }

    fun getNameByType(type: Int): Int {
        // TODO
        return R.string.app_name
    }

}