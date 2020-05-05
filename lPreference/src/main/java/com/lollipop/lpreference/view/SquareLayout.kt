package com.lollipop.lpreference.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @author lollipop
 * @date 2020-02-05 20:16
 * 方形的容器
 */
open class SquareLayout(context: Context, attrs: AttributeSet?, defStyleAttr:Int)
    : FrameLayout(context,attrs,defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    /**
     * 允许主动设定尺寸
     */
    var orientation = Orientation.NONE

    enum class Orientation(val value: Int) {
        NONE(-1),
        VERTICAL(0),
        HORIZONTAL(1)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (orientation == Orientation.HORIZONTAL) {
            if (heightSize <= 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                heightSize = measuredHeight
            }
            heightMode = MeasureSpec.EXACTLY
            widthMode = MeasureSpec.UNSPECIFIED
        } else if (orientation == Orientation.VERTICAL) {
            if (widthSize <= 0) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                widthSize = measuredWidth
            }
            heightMode = MeasureSpec.UNSPECIFIED
            widthMode = MeasureSpec.EXACTLY
        }

        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            // 如果横向确定，纵向不确定，那么扩展纵向
            heightSize = widthSize
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            // 如果纵向固定，横向不固定，那么横向扩展
            widthSize = heightSize
        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY))
    }

}