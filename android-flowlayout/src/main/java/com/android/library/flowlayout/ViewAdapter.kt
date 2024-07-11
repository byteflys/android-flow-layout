package com.android.library.flowlayout

import android.view.View

interface ViewAdapter<T : Any, V : View> {
    fun createViewFrom(data: T): V
}

fun <T : Any, V : View> FlowLayout.applyViewAdapter(adapter: ViewAdapter<T, V>, datas: List<T>) {
    removeAllViews()
    for (element in datas) {
        val view = adapter.createViewFrom(element)
        addView(view)
    }
    requestLayout()
}