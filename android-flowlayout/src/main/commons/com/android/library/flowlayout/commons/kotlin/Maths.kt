package com.android.library.flowlayout.commons.kotlin

internal object Maths {

    fun findInsertIndexAsc(list: List<Int>, value: Int): Int {
        for (i in list.size downTo 1) {
            val item = list[i - 1]
            if (item == value)
                return -1
            if (item < value)
                return i
        }
        return 0
    }
}