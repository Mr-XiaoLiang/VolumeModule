package com.lollipop.lpreference.dialog


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.lpreference.PreferenceHelper
import com.lollipop.lpreference.R
import com.lollipop.lpreference.util.*
import com.lollipop.lpreference.value.ColorArray
import com.lollipop.lpreference.view.CirclePointView
import com.lollipop.lpreference.view.HuePaletteView
import com.lollipop.lpreference.view.SatValPaletteView
import com.lollipop.lpreference.view.TransparencyPaletteView
import kotlinx.android.synthetic.main.fragment_colors_panel_dialog.*
import kotlinx.android.synthetic.main.fragment_palette.*

/**
 * 颜色选择的面板
 * @author Lollipop
 */
class ColorsPanelDialogFragment: BaseDialog(),
    HuePaletteView.HueCallback, SatValPaletteView.HSVCallback,
    TransparencyPaletteView.TransparencyCallback,
    View.OnClickListener {

    companion object {
        fun show(context: Context, selectedColor: ColorArray, max: Int = -1, callback: (IntArray) -> Unit) {
            ColorsPanelDialogFragment().apply {
                selectedData.clear()
                for (color in selectedColor) {
                    selectedData.add(color)
                }
                maxSelected = max
                selectedColorCallback = callback
            }.show(context, "ColorsPanelDialogFragment")
        }

        private val ACTION_PALETTE = R.drawable.ic_palette_black_24dp
        private val ACTION_DELETE = R.drawable.ic_delete_black_24dp
        private val ACTION_ADD = R.drawable.ic_add_black_24dp
        private const val PREFERENCE_KEY = "KEY_USER_COLORS"
    }

    override val contextId: Int
        get() = R.layout.fragment_colors_panel_dialog

    private val hsvTemp = FloatArray(3)
    private var colorRGB = Color.RED
    private var colorAlpha = 255
    private var maxSelected = -1

    private val shownData = ArrayList<Int>()

    private val selectedData = ArrayList<Int>()

    private val shownActions = ArrayList<Int>()

    private var isEditMode = false

    private var selectedColorCallback: ((IntArray) -> Unit)? = null

    private val spanCount = 4

    private val itemAdapter: ItemAdapter by lazy {
        ItemAdapter(shownData, {
            isItemChecked(it)
        }, {
            onItemClick(it)
        }, shownActions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        huePalette.hueCallback = this
        satValPalette.hsvCallback = this
        transparencyPalette.transparencyCallback = this
        definiteBtn.setOnClickListener(this)
        if (backBtn.isPortrait()) {
            backBtn.setOnClickListener(this)
        } else {
            backBtn.visibility = View.GONE
        }

        colorPoolView.layoutManager = GridLayoutManager(colorPoolView.context,
            spanCount, RecyclerView.VERTICAL, false).apply {
        }
        colorPoolView.adapter = itemAdapter
        if (poolPanel.isPortrait()) {
            poolPanel.post {
                colorPoolView.layoutParams = colorPoolView.layoutParams.apply {
                    width = poolPanel.width
                }
            }
        }
        initData()
    }

    private fun initData() {
        if (maxSelected > 0) {
            while (selectedData.size > maxSelected) {
                selectedData.removeAt(selectedData.size - 1)
            }
        }
        getColorList()
        if (shownData.isEmpty()) {
            val defColors = resources.getIntArray(R.array.def_colors)
            for (color in defColors) {
                shownData.add(color)
            }
        }
        for (color in selectedData) {
            if (!ItemAdapter.hasColor(shownData, color)) {
                shownData.add(color)
            }
        }
        initPalette(if (shownData.isEmpty()) { Color.RED } else { shownData[shownData.size - 1] } )
        onModeChange()
        updateSelectedItem()
    }

    override fun onDestroyView() {
        saveColorList()
        val result = IntArray(selectedData.size) { index -> selectedData[index] }
        selectedColorCallback?.invoke(result)
        super.onDestroyView()
    }

    private fun getColorList() {
        context?.let {
            val value = PreferenceHelper.get(it, PREFERENCE_KEY, "")
            val colorArray = stringToList(value)
            shownData.clear()
            shownData.addAll(colorArray)
        }
    }

    private fun saveColorList() {
        context?.let {
            val value = listToString(shownData)
            PreferenceHelper.put(it, PREFERENCE_KEY, value)
        }
    }

    private fun stringToList(value: String): Array<Int> {
        if (value.isEmpty()) {
            return arrayOf()
        }
        val values = value.split(":")
        val result = Array(values.size) { 0 }
        for (index in values.indices) {
            val color = try {
                values[index].toInt(16)
            } catch (e: Exception) {
                0
            }
            result[index] = color
        }
        return result
    }

    private fun listToString(list: ArrayList<Int>): String {
        val builder = StringBuilder()
        for (index in list.indices) {
            if (index > 0) {
                builder.append(":")
            }
            builder.append(list[index].toString(16))
        }
        return builder.toString()
    }

    private fun initPalette(color: Int) {
        Color.colorToHSV(color, hsvTemp)
        huePalette.parser(hsvTemp[0])
        satValPalette.parser(hsvTemp[1], hsvTemp[2])
        transparencyPalette.parser(Color.alpha(color))
    }

    override fun onHueSelect(hue: Int) {
        satValPalette.onHueChange(hue.toFloat())
    }

    override fun onHSVSelect(hsv: FloatArray, rgb: Int) {
        colorRGB = rgb
        onColorChange()
    }

    override fun onTransparencySelect(alphaF: Float, alphaI: Int) {
        colorAlpha = alphaI
        onColorChange()
    }

    private fun merge(alpha: Int = colorAlpha, rgb: Int = colorRGB): Int {
        val a = alpha.range(0, 255) shl 24
        return rgb and 0xFFFFFF or a
    }

    private fun onColorChange() {
        val color = merge()
        previewColorView.setStatusColor(color)
        colorValueView.text = color.colorValue()
    }

    private fun openPalette() {
        if (!palettePanel.isPortrait()) {
            return
        }
        if (palettePanel.translationX < 1) {
            palettePanel.translationX = palettePanel.width * 1F
        }
        palettePanel.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(0F)

            animator.lifecycleBinding {
                onStart {
                    palettePanel.visibility = View.VISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }

        poolPanel.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(poolPanel.width * -1F)
            animator.lifecycleBinding {
                onStart {
                    poolPanel.visibility = View.INVISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }
    }

    private fun closePalette() {
        if (!palettePanel.isPortrait()) {
            return
        }
        palettePanel.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(palettePanel.width * 1F)
            animator.lifecycleBinding {
                onEnd {
                    palettePanel.visibility = View.INVISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }

        poolPanel.animate().let { animator ->
            animator.cancel()
            changeInterpolator(animator)
            animator.translationX(0F)
            animator.lifecycleBinding {
                onStart {
                    poolPanel.visibility = View.VISIBLE
                    removeThis(it)
                }
            }
            animator.start()
        }
    }

    private fun changeInterpolator(animator: ViewPropertyAnimator) {
        if (animator.interpolator !is DecelerateInterpolator) {
            animator.interpolator = DecelerateInterpolator()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            definiteBtn -> {
                itemAdapter.addItem(merge())
                closePalette()
            }
            backBtn -> {
                closePalette()
            }
        }
    }

    private fun isItemChecked(position: Int): Int {
        if (isEditMode) {
            return 0
        }
        if (position < 0 || position >= shownData.size) {
            return 0
        }
        val color = shownData[position]
        for (index in selectedData.indices) {
            if (selectedData[index] == color) {
                log("isItemChecked position=$position, index=$index")
                val result = index + 1
                if (maxSelected == 1 && result == 1) {
                    return -1
                }
                return result
            }
        }
        return 0
    }

    private fun onItemClick(holder: RecyclerView.ViewHolder): Boolean {
        if (ItemAdapter.isAction(holder)) {
            onActionSelected(ItemAdapter.getAction(holder))
            return true
        }
        val position = holder.adapterPosition
        if (position < 0 || position >= shownData.size) {
            return false
        }
        val color = shownData[position]
        if (isEditMode) {
            itemAdapter.removeItem(color)
            selectedData.remove(color)
            updateSelectedItem()
            return false
        }
        for (index in selectedData.indices) {
            if (selectedData[index] == color) {
                selectedData.removeAt(index)
                updateSelectedItem(index)
                return true
            }
        }
        if (maxSelected  == 1) {
            while (selectedData.size > 0) {
                val old = shownData.indexOf(selectedData.removeAt(0))
                itemAdapter.notifyItemChanged(old)
            }
            selectedData.add(color)
            updateSelectedItem(0)
            return true
        }
        if (maxSelected > 0 && selectedData.size >= maxSelected) {
            return false
        }
        selectedData.add(color)
        updateSelectedItem()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun updateSelectedItem(start: Int = -1) {
        if (maxSelected > 0) {
            selectedSizeView.text = "${selectedData.size}/$maxSelected"
        }
        if (start < 0 || start >= selectedData.size) {
            return
        }
        for (index in start until selectedData.size) {
            itemAdapter.notifyItemChanged(shownData.indexOf(selectedData[index]))
        }
    }

    private fun onActionSelected(actionId: Int) {
        when (actionId) {
            ACTION_PALETTE -> {
                if (isEditMode) {
                    isEditMode = false
                    onModeChange()
                }
            }
            ACTION_DELETE -> {
                if (!isEditMode) {
                    isEditMode = true
                    onModeChange()
                }
            }
            ACTION_ADD -> {
                openPalette()
            }
        }
    }

    private fun onModeChange() {
        shownActions.clear()
        if (isEditMode) {
            shownActions.add(ACTION_PALETTE)
            modeStatusView.setImageResource(ACTION_DELETE)
        } else {
            shownActions.add(ACTION_DELETE)
            if (modeStatusView.isPortrait()) {
                shownActions.add(ACTION_ADD)
            }
            modeStatusView.setImageResource(ACTION_PALETTE)
        }
        // 添加空内容
        for (i in 0 until spanCount) {
            shownActions.add(0)
        }
        itemAdapter.notifyDataSetChanged()
    }

    private class ItemAdapter(private val data: ArrayList<Int>,
                              private val isChecked: (Int) -> Int,
                              private val onClick: (RecyclerView.ViewHolder) -> Boolean,
                              private val actionList: ArrayList<Int>):
        RecyclerView.Adapter<ItemHolder>() {

        companion object {

            private const val TYPE_ITEM = 0
            private const val TYPE_ACTION = 1

            fun getAction(holder: RecyclerView.ViewHolder): Int {
                if (holder is ActionHolder) {
                    return holder.iconId
                }
                return 0
            }

            fun isAction(holder: RecyclerView.ViewHolder): Boolean {
                return holder is ActionHolder
            }

            fun hasColor(list: ArrayList<Int>, target: Int): Boolean {
                for (color in list) {
                    if (target == color) {
                        return true
                    }
                }
                return false
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return when (viewType) {
                TYPE_ACTION -> {
                    ActionHolder.create(parent, onClick)
                }
                else -> {
                    ItemHolder.create(parent, isChecked, onClick)
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size + actionList.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < data.size) {
                TYPE_ITEM
            } else {
                TYPE_ACTION
            }
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            when (getItemViewType(position)) {
                TYPE_ITEM -> {
                    holder.onBind(data[position])
                }
                TYPE_ACTION -> {
                    holder.onBind(actionList[position - data.size])
                }
            }
        }

        fun addItem(color: Int) {
            if (hasColor(data, color)) {
                return
            }
            data.add(color)
            notifyItemInserted(data.size - 1)
        }

        fun removeItem(color: Int) {
            var index = -1
            for (i in data.indices) {
                if (color == data[i]) {
                    index = i
                    break
                }
            }
            if (index >= 0) {
                data.removeAt(index)
                notifyItemRemoved(index)
            }
        }

    }

    private open class ItemHolder (view: View,
                            protected val isChecked: (Int) -> Int,
                            protected val onClick: (RecyclerView.ViewHolder) -> Boolean):
        RecyclerView.ViewHolder(view) {

        companion object {

            fun createView(viewGroup: ViewGroup): View {
                return LayoutInflater.from(viewGroup.context).inflate(
                    R.layout.item_color_panel, viewGroup, false)
            }

            fun create(viewGroup: ViewGroup,
                       isChecked: (Int) -> Int,
                       onClick: (RecyclerView.ViewHolder) -> Boolean): ItemHolder {
                return ItemHolder(createView(viewGroup), isChecked, onClick)
            }
        }

        protected val colorView: CirclePointView = itemView.findViewById(R.id.colorView)
        protected val borderView: ImageView = itemView.findViewById(R.id.borderView)

        private var isLocked = false

        init {
            itemView.setOnClickListener {
                if (!isLocked && onClick(this)) {
                    updateCheckedStatus()
                }
            }
            colorView.apply {
                setTextColor(Color.WHITE)
                setShadowLayer(2F, 1F, 1F, Color.BLACK)
            }
        }

        open fun onBind(value: Int) {
            setColor(value)
            updateCheckedStatus(false)
            colorView.requestLayout()
        }

        private fun updateCheckedStatus(isAnim: Boolean = true) {
            val status = isChecked(this.adapterPosition)
            val checked = status != 0
            val value = if (status > 0) { status.toString() } else { "" }
            setChecked(checked, value, isAnim)
        }

        protected fun setColor(color: Int) {
            colorView.setStatusColor(color)
        }

        protected fun setChecked(isChecked: Boolean, number: String = "", animation: Boolean = true) {
            log("setChecked: isChecked=$isChecked, number=$number, animation=$animation")
            if (animation) {
                isLocked = true
                val start = if (isChecked) { 0F } else { 1F }
                borderView.scaleX = start
                borderView.scaleY = start
                val end = if (isChecked) { 1F } else {0F }
                borderView.animate().let { animator ->
                    animator.cancel()
                    animator.scaleX(end).scaleY(end)
                    animator.lifecycleBinding {
                        onStart {
                            if (isChecked) {
                                borderView.visibility = View.VISIBLE
                                removeThis(it)
                            }
                        }
                        onEnd {
                            isLocked = false
                            if (!isChecked) {
                                borderView.visibility = View.INVISIBLE
                            }
                            removeThis(it)
                        }
                        onCancel {
                            removeThis(it)
                        }
                    }
                    animator.start()
                }
            } else {
                isLocked = false
                borderView.scaleX = 1F
                borderView.scaleY = 1F
                borderView.visibility = if (isChecked) { View.VISIBLE } else { View.INVISIBLE }
            }
            colorView.autoText = (if (isChecked) { number } else { "" })
        }

    }

    private class ActionHolder private constructor(view: View,
                                                  onClick: (RecyclerView.ViewHolder) -> Boolean):
        ItemHolder(view, { 0 }, onClick) {

        companion object {
            fun create(viewGroup: ViewGroup,
                       onClick: (RecyclerView.ViewHolder) -> Boolean): ActionHolder {
                return ActionHolder(createView(viewGroup), onClick)
            }
        }

        var iconId = 0
            private set

        init {
            colorView.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            itemView.setOnClickListener {
                onClick(this)
            }
        }

        override fun onBind(value: Int) {
            if (value != 0) {
                itemView.isClickable = true
                setChecked(true, "", false)
            } else {
                itemView.isClickable = false
                setChecked(false, "", false)
            }
            iconId = value
            borderView.setImageResource(value)
            setColor(Color.TRANSPARENT)
        }
    }

}