package com.lollipop.lpreference.value

/**
 * @author lollipop
 * @date 2020-02-08 20:01
 * 颜色的数组
 */
class ColorArray: PreferenceValue {

    private val valueArray = ArrayList<Int>()

    operator fun get(index: Int): Int {
        return valueArray[index]
    }

    fun add(color: Int) {
        valueArray.add(color)
    }

    operator fun set(index: Int, color: Int) {
        valueArray[index] = color
    }

    val size: Int
        get() = valueArray.size

    fun clear() {
        valueArray.clear()
    }

    fun removeAt(index: Int) {
        valueArray.removeAt(index)
    }

    fun remove(color: Int) {
        valueArray.remove(color)
    }

    fun addAll(colors: ArrayList<Int>) {
        valueArray.addAll(colors)
    }

    fun addAll(colors: ColorArray) {
        addAll(colors.valueArray)
    }

    fun addAll(vararg colors: Int) {
        for (color in colors) {
            add(color)
        }
    }

    fun values(): ArrayList<Int> {
        return valueArray
    }

    override fun serialization(): String {
        return listToString(valueArray)
    }

    private fun stringToList(value: String) {
        clear()
        if (value.isEmpty()) {
            return
        }
        val values = value.split(":")
        for (index in values.indices) {
            try {
                val color = values[index].toInt(16)
                add(color)
            } catch (e: Exception) {
            }
        }
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

    override fun parse(value: String) {
        stringToList(value)
    }

    operator fun iterator(): Iterator<Int> {
        return valueArray.iterator()
    }

    fun newInstance(): ColorArray {
        val array = ColorArray()
        array.addAll(valueArray)
        return array
    }

}