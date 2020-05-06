package com.lollipop.volumemodule.provider

import android.content.Context
import android.view.View
import com.lollipop.volumemodule.R
import com.lollipop.volumemodule.volume.StreamType
import com.lollipop.volumemodule.volume.VolumeController

/**
 * @author lollipop
 * @date 2020/5/6 14:57
 * H2OS的Provider
 */
class H2osProvider(context: Context,
                   controller: VolumeController,
                   providerController: ProviderController)
    : BaseProvider(context, controller, providerController) {

    override val providerName: Int
        get() = R.string.h2os10

    override fun createView(): View {
        return createViewById(R.layout.provider_h2os)
    }

    override fun onVolumeChanged(type: StreamType) {
        TODO("Not yet implemented")
    }


}