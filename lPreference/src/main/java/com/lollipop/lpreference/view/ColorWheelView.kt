package com.lollipop.lpreference.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.lollipop.lpreference.value.ColorArray
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-02-08 20:15
 * 色轮的View
 */
class ColorWheelView(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
    ImageView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null)

    private val colorWheelDrawable = ColorWheelDrawable()

    init {
        setImageDrawable(colorWheelDrawable)
        if (isInEditMode) {
            setColor(ColorArray().apply {
                addAll(Color.RED, Color.BLUE, Color.GREEN, Color.CYAN)
            })
        }
    }

    fun setColor(colorArray: ColorArray) {
        colorWheelDrawable.setColor(colorArray)
    }

    var isGradient: Boolean
        get() {
            return colorWheelDrawable.isGradient
        }
        set(value) {
            colorWheelDrawable.isGradient = value
        }

    private class ColorWheelDrawable: Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
        }

        private var shader: Shader? = null

        private var radius = 0F

        private val arcRectF = RectF()

        var isGradient = true
            set(value) {
                field = value
                invalidateSelf()
            }

        private val colors = ColorArray()

        fun setColor(colorArray: ColorArray) {
            colors.clear()
            colors.addAll(colorArray)
            resetShader()
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            if (colors.size < 1) {
                return
            }
            if (colors.size == 1) {
                paint.color = colors[0]
                paint.shader = null
                canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                    radius, paint)
                return
            }
            if (isGradient) {
                paint.shader = shader
                canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(),
                    radius, paint)
            } else {
                paint.shader = null
                val step = 360F / colors.size
                for (index in 0 until colors.size) {
                    paint.color = colors[index]
                    drawArc(canvas, (index * step) - (step / 2), step)
                }
            }
        }

        private fun drawArc(canvas: Canvas, startAngle: Float, length: Float) {
            canvas.drawArc(arcRectF,
                startAngle, length, false, paint)
        }

        override fun onBoundsChange(bound: Rect?) {
            super.onBoundsChange(bound)
            resetShader()
        }

        private fun resetShader() {
            val r = min(bounds.width(), bounds.height()) * 0.5F
            val strokeWidth = r * 0.6F
            paint.strokeWidth = strokeWidth
            radius = r - (strokeWidth / 2)

            val centerX = bounds.exactCenterX()
            val centerY = bounds.exactCenterY()
            val left = centerX - radius
            val top = centerY - radius
            val right = centerX + radius
            val bottom = centerY + radius
            arcRectF.set(left, top, right, bottom)

            shader = if (colors.size > 1 && bounds.width() > 0 && bounds.height() > 0) {
                SweepGradient(bounds.exactCenterX(), bounds.exactCenterY(),
                    IntArray(colors.size + 1) { colors[it % colors.size] }, null)
            } else {
                null
            }
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

    }

}