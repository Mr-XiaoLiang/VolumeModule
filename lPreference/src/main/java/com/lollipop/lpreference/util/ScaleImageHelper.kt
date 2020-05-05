package com.lollipop.lpreference.util

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


/**
 * @author lollipop
 * @date 2020-02-12 00:11
 * 图片缩放的辅助类
 */
class ScaleImageHelper private constructor(private val imageView: ImageView): View.OnTouchListener,
    ScaleGestureDetector.OnScaleGestureListener {

    private val suppMatrix: Matrix = Matrix()
    private val baseMatrix = Matrix()
    private val drawableMatrix = Matrix()
    private val displayRect = RectF()
    private var viewWidth = 0
    private var viewHeight = 0
//    private val downTouch = PointF()
    private val offset = PointF(0F, 0F)
//    private var pointerId = 0

    companion object {
        @SuppressLint("ClickableViewAccessibility")
        fun with(view: ImageView): ScaleImageHelper {
            val helper = ScaleImageHelper(view)
            view.setOnTouchListener(helper)
            return helper
        }
    }

    init {
        imageView.post {
            viewWidth = imageView.width - imageView.paddingLeft - imageView.paddingRight
            viewHeight = imageView.height - imageView.paddingTop - imageView.paddingBottom
            notifyScaleChange()
        }
    }

    private val scaleGestureDetector = ScaleGestureDetector(imageView.context, this)

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {
    }

    override fun onScale(detector: ScaleGestureDetector?): Boolean {
        detector?:return false
        log("$objId, onScale")
        val scaleFactor = detector.scaleFactor
        val focusX = detector.focusX
        val focusY = detector.focusY
        if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor)) {
            return false
        }
        suppMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY)
        notifyScaleChange()
        return true
    }

    fun reset() {
        imageView.imageMatrix = null
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    private fun notifyScaleChange() {
        if(checkMatrixBounds()) {
            log("$objId, notifyScaleChange")
            imageView.scaleType = ImageView.ScaleType.MATRIX
            imageView.imageMatrix = getDrawMatrix()
        }
    }

    private fun checkMatrixBounds(): Boolean {
        val rect = getDisplayRect(getDrawMatrix()) ?: return false

        val height = rect.height()
        val width = rect.width()
        var deltaX = 0f
        var deltaY = 0f

        when {
            height <= viewHeight -> {
                deltaY = (viewHeight - height) / 2 - rect.top
                offset.y = 0F
            }
            rect.top > 0 -> {
                deltaY = -rect.top + offset.y
            }
            rect.bottom < viewHeight -> {
                deltaY = viewHeight - rect.bottom + offset.y
            }
        }
        when {
            width <= viewWidth -> {
                deltaX = (viewWidth - width) / 2 - rect.left
                offset.x = 0F
            }
            rect.left > 0 -> {
                deltaX = -rect.left + offset.x
            }
            rect.right < viewWidth -> {
                deltaX = viewWidth - rect.right + offset.x
            }
        }
        suppMatrix.postTranslate(deltaX, deltaY)
        return true
    }

    private fun getDisplayRect(matrix: Matrix): RectF? {
        return imageView.drawable?.let { drawable ->
            displayRect.set(0F, 0F,
                drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
            matrix.mapRect(displayRect)
            displayRect
        }
    }

    private fun getDrawMatrix(): Matrix {
        drawableMatrix.set(baseMatrix)
        drawableMatrix.postConcat(suppMatrix)
        return drawableMatrix
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?:return false
        v?:return false
        if (v.parent is ViewGroup) {
            v.parent.requestDisallowInterceptTouchEvent(true)
        }
//        var touchStatus = false
//        when (event.action) {
//            MotionEvent.ACTION_DOWN -> {
//                log("$objId, onDown")
//                downTouch.set(event.x, event.y)
//                offset.set(0F, 0F)
//                touchStatus = true
//                pointerId = event.getPointerId(0)
//            }
//            MotionEvent.ACTION_MOVE -> if (event.pointerCount == 1) {
//                // 如果只有一个手指头，那么做移动手势
//                val x = event.xById()
//                val y = event.yById()
//                offset.x += x - downTouch.x
//                offset.y += y - downTouch.y
//                downTouch.set(x, y)
//                notifyScaleChange()
//                log("$objId, onMove: pointerId=$pointerId, x=$x, y=$y, offset=$offset")
//                touchStatus = true
//            }
//            MotionEvent.ACTION_UP -> {
//                log("$objId, onUp")
//                offset.set(0F, 0F)
//            }
//            MotionEvent.ACTION_POINTER_UP -> {
//                log("$objId, onPointerUp")
//                if (event.pointerCount == 1) {
//                    downTouch.set(event.xById(), event.yById())
//                }
//            }
//            MotionEvent.ACTION_CANCEL -> {
//                log("$objId, onCancel")
//                return false
//            }
//        }
        return scaleGestureDetector.onTouchEvent(event) // || touchStatus
    }

//    private fun MotionEvent.checkPointId() {
//        val pointerIndex = this.findPointerIndex(pointerId)
//        if (pointerIndex < 0) {
//            pointerId = this.getPointerId(0)
//        }
//    }

//    private fun MotionEvent.xById(): Float {
//        checkPointId()
//        return this.getX(this.findPointerIndex(pointerId))
//    }
//
//    private fun MotionEvent.yById(): Float {
//        checkPointId()
//        return getY(findPointerIndex(pointerId))
//    }

}