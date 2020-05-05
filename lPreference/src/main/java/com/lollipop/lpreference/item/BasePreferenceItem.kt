package com.lollipop.lpreference.item

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.lollipop.lpreference.PreferenceConfig
import com.lollipop.lpreference.R
import com.lollipop.lpreference.info.BasePreferenceInfo

/**
 * @author lollipop
 * @date 2020-01-16 23:08
 * 基础的偏好设置项
 */
abstract class BasePreferenceItem <T : BasePreferenceInfo<*>> (group: ViewGroup) :
    PreferenceItem<T>(createView(group, DEF_LAYOUT)),
    View.OnClickListener{

    companion object {
        private val DEF_LAYOUT = R.layout.item_base_preference
    }

    private var isInit = false

    abstract val widgetId: Int

    protected val preferenceKey: String
        get() {
            return preferenceInfo?.key?:""
        }

    protected var preferenceInfo: T? = null
        private set

    protected val context: Context
        get() {
            return itemView.context
        }

    protected var isEnable: Boolean = true
        private set

    private var relevantStatusCallback: ((Int) -> Boolean)? = null

    private var preferenceChangeCallback: ((Int) -> Unit)? = null

    private val notifyItemChangeTask: Runnable by lazy {
        Runnable {
            preferenceChangeCallback?.invoke(adapterPosition)
        }
    }

    private val titleView: TextView by lazy {
        itemView.setOnClickListener(this)
        itemView.findViewById<TextView>(R.id.titleView).apply {
            setTextColor(PreferenceConfig.titleColor)
        }
    }

    private val summaryView: TextView by lazy {
        itemView.findViewById<TextView>(R.id.summaryView).apply {
            setTextColor(PreferenceConfig.summaryColor)
        }
    }
    private val iconView: ImageView by lazy {
        itemView.findViewById<ImageView>(R.id.iconView).apply {
            outlineProvider = ViewOutlineProvider.BACKGROUND
            imageTintList = ColorStateList.valueOf(PreferenceConfig.iconColor)
        }
    }
    private val previewBody: FrameLayout by lazy {
        itemView.findViewById<FrameLayout>(R.id.previewBody).apply {
            setOnClickListener(this@BasePreferenceItem)
        }
    }

    fun init(relevantCallback: (Int) -> Boolean,
             applyCallback: (Int) -> Unit) {
        if (isInit) {
            return
        }
        isInit = true
        this.relevantStatusCallback = relevantCallback
        this.preferenceChangeCallback = applyCallback
        if (widgetId != 0) {
            bindPreview(widgetId)
        }
        onInit(itemView)
    }

    protected open fun onInit(view: View) {}

    private fun bindPreview(view: View) {
        if (previewBody.childCount > 0) {
            previewBody.removeAllViews()
        }
        if (previewBody.visibility != View.VISIBLE) {
            previewBody.visibility = View.VISIBLE
        }
        previewBody.addView(view)
    }

    private fun bindPreview(layoutId: Int): View {
        val inflater = LayoutInflater.from(previewBody.context)
        val view = inflater.inflate(layoutId, previewBody, false)
        bindPreview(view)
        return view
    }

    protected fun setIcon(resId: Int) {
        if (resId != 0) {
            if (iconView.visibility != View.VISIBLE) {
                iconView.visibility = View.VISIBLE
            }
            iconView.setImageResource(resId)
        } else {
            if (iconView.visibility != View.GONE) {
                iconView.visibility = View.GONE
            }
        }
    }

    protected open fun onPreviewClick(view: View): Boolean {
        return false
    }

    protected open fun onItemClick(view: View) { }

    protected var title: CharSequence
        get() {
            return titleView.text
        }
        private set(value) {
            titleView.text = value
        }

    protected var summary: CharSequence
        get() {
            return summaryView.text
        }
        set(value) {
            summaryView.text = value
        }

    override fun bind(info: T) {
        preferenceInfo = info
        title = info.title
        summary = info.summary
        setIcon(info.iconId)
        onBind(info)
        checkItemStatus(relevantStatusCallback?.invoke(adapterPosition)?:true)
    }

    protected fun notifyInfoChange() {
        preferenceInfo?.let { bind(it) }
        applyChange()
    }

    protected open fun onBind(info: T) { }

    private fun checkItemStatus(isEnable: Boolean) {
        this.isEnable = isEnable
        itemView.isEnabled = isEnable
        titleView.setStatus(isEnable)
        summaryView.setStatus(isEnable)
        iconView.alpha = if (isEnable) { 1F } else { 0.5F }
        onStatusChange(isEnable)
    }

    protected fun applyChange() {
        itemView.removeCallbacks(notifyItemChangeTask)
        itemView.postDelayed(notifyItemChangeTask, 50)
    }

    protected fun View.setStatus(isEnable: Boolean) {
        this.alpha = if (isEnable) {
            1F
        } else {
            0.5F
        }
    }

    protected open fun onStatusChange(isEnable: Boolean) {}

    override fun onClick(v: View?) {
        if (!isEnable) {
            return
        }
        when(v) {
            itemView -> {
                onItemClick(v)
            }
            previewBody -> {
                if (!onPreviewClick(v)) {
                    onItemClick(v)
                }
            }
        }
    }

}