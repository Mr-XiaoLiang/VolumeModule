package com.lollipop.lpreference.value

import android.net.Uri

/**
 * @author lollipop
 * @date 2020-02-08 20:01
 * 颜色的数组
 */
class ImageArray: PreferenceValue {

    private val valueArray = ArrayList<Uri>()

    operator fun get(index: Int): Uri {
        return valueArray[index]
    }

    fun getList(): ArrayList<Uri> {
        return valueArray
    }

    fun add(image: Uri) {
        valueArray.add(image)
    }

    operator fun set(index: Int, image: Uri) {
        valueArray[index] = image
    }

    val size: Int
        get() = valueArray.size

    fun clear() {
        valueArray.clear()
    }

    fun addAll(images: ArrayList<Uri>) {
        valueArray.addAll(images)
    }

    fun addAll(colors: ImageArray) {
        addAll(colors.valueArray)
    }

    fun addAll(vararg images: Uri) {
        for (image in images) {
            add(image)
        }
    }

    override fun serialization(): String {
        return listToString(valueArray)
    }

    private fun stringToList(value: String) {
        clear()
        if (value.isEmpty()) {
            return
        }
        val values = value.split(";")
        for (index in values.indices) {
            try {
                val color = Uri.parse(values[index])
                add(color)
            } catch (e: Exception) {
            }
        }
    }

    private fun listToString(list: ArrayList<Uri>): String {
        val builder = StringBuilder()
        for (index in list.indices) {
            if (index > 0) {
                builder.append(";")
            }
            builder.append(list[index].toString())
        }
        return builder.toString()
    }

    override fun parse(value: String) {
        stringToList(value)
    }

    operator fun iterator(): Iterator<Uri> {
        return valueArray.iterator()
    }

}