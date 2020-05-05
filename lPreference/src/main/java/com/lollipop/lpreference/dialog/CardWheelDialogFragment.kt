package com.lollipop.lpreference.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.StringToColorUtil
import com.lollipop.lpreference.util.banner.LBannerLayoutManager
import com.lollipop.lpreference.util.banner.Orientation
import com.lollipop.lpreference.util.banner.ScrollState
import com.lollipop.lpreference.util.changeAlpha
import com.lollipop.lpreference.util.range
import kotlinx.android.synthetic.main.dialog_card_wheel.*

/**
 * @author lollipop
 * @date 2020-01-21 17:09
 */
class CardWheelDialogFragment private constructor(): BaseDialog() {

    private var isTouched = false
    private var isViewCreated = false
    private var selectedIndex = -1

    override val contextId: Int
        get() = R.layout.dialog_card_wheel

    private val dataList = ArrayList<CardInfo>()

    private var onSelectedListener: OnSelectedListener? = null

    companion object {
        fun show(context: Context, data: List<CardInfo>,
                 selected: Int, tag: String = "CardWheel", listener: OnSelectedListener? = null) {
            CardWheelDialogFragment().apply {
                dataList.clear()
                dataList.addAll(data)
                selectedIndex = selected
                onSelectedListener = listener
            }.show(context, tag)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardGroup.layoutManager = LBannerLayoutManager().apply {
            orientation = Orientation.HORIZONTAL
            onSelectedChange { position, state ->
                if (state == ScrollState.DRAGGING) {
                    isTouched = true
                }
                if (state == ScrollState.IDLE) {
                    selectedIndex = position
                    if (selectedIndex >= 0 && isViewCreated && isTouched) {
                        onSelectedListener?.onCardSelected(dataList[selectedIndex])
                    }
                }
            }
        }
        cardGroup.adapter =
            WheelAdapter(
                dataList,
                layoutInflater
            )
        LinearSnapHelper().attachToRecyclerView(cardGroup)
        isViewCreated = true
    }

    override fun onResume() {
        super.onResume()
        if (dataList.isEmpty()) {
            return
        }
        cardGroup.smoothScrollToPosition(selectedIndex.range(0, dataList.size))
    }

    interface OnSelectedListener {
        fun onCardSelected(info: CardInfo)
    }

    open class CardInfo(val name: String)

    private class WheelAdapter(
        private val data: ArrayList<CardInfo>,
        private val layoutInflater: LayoutInflater): RecyclerView.Adapter<ItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder.create(
                layoutInflater,
                parent
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.onBind(data[position])
        }

    }

    private class ItemHolder private constructor(view: View): RecyclerView.ViewHolder(view) {

        companion object {
            fun create(layoutInflater: LayoutInflater, parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    layoutInflater.inflate(
                        R.layout.item_card_wheel, parent, false
                    )
                )
            }
        }

        private val dayView: TextView = view.findViewById(R.id.valueView)

        fun onBind(info: CardInfo) {
            val color =
                StringToColorUtil.format(info.name)
            dayView.setBackgroundColor(color.changeAlpha())
            dayView.text = info.name
        }

    }


}