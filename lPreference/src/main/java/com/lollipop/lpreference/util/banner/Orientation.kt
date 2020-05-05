package com.lollipop.lpreference.util.banner

import androidx.recyclerview.widget.RecyclerView

/**
 * 方向的枚举类
 * 用于控制Banner的滚动方向
 * @author Lollipop
 */
enum class Orientation(val value: Int) {

    /**
     * 横向滚动
     */
    HORIZONTAL(RecyclerView.HORIZONTAL),
    /**
     * 纵向滚动
     */
    VERTICAL(RecyclerView.VERTICAL)

}