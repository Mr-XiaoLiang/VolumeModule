package com.lollipop.lpreference.util

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min

/**
 * @author lollipop
 * @date 2020-01-18 20:11
 * 常见的工具方法
 */

fun Int.changeAlpha(alpha: Int = 0x16): Int {
    val a = max(min(alpha, 255), 0) shl 24
    return this and 0xFFFFFF or a
}

fun Int.changeAlpha(weight: Float): Int {
    return changeAlpha((this.alpha * weight).toInt())
}

inline val Int.alpha get() = Color.alpha(this)

fun Int.range(min: Int, max: Int): Int {
    if(this < min) {
        return min
    }
    if (this > max) {
        return max
    }
    return this
}

fun View.getColor(id: Int): Int {
    return ContextCompat.getColor(context, id)
}

fun Int.colorValue(): String {
    var result = "#"
    result += Color.alpha(this).toHexString()
    result += Color.red(this).toHexString()
    result += Color.green(this).toHexString()
    result += Color.blue(this).toHexString()
    return result
}

fun Int.toHexString(digit: Int = 2): String {
    val builder = StringBuilder(Integer.toHexString(this))
    while (builder.length < digit) {
        builder.insert(0, "0")
    }
    return builder.toString().toUpperCase(Locale.US)
}

fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun View.isPortrait(): Boolean {
    return context.isPortrait()
}

fun Any.logger(tag: String = this.javaClass.simpleName): (String) -> Unit {
    return {
        Log.d("Lollipop-$tag" , it)
    }
}

fun Any.log(value: String) {
    logger()(value)
}

val Any.objId: String
    get() {
        return System.identityHashCode(this).toString(16)
    }

private val threadPool: Executor by lazy {
    Executors.newCachedThreadPool()
}

private val mainHandler: Handler by lazy {
    Handler(Looper.getMainLooper())
}

fun <T> T.doAsync(error: ((Throwable) -> Unit)? = null, run: T.() -> Unit) {
    threadPool.execute {
        try {
            run.invoke(this)
        } catch (e: Throwable) {
            error?.invoke(e)
        }
    }
}

fun <T> T.onUI(run: T.() -> Unit) {
    mainHandler.post {
        run.invoke(this)
    }
}
