package com.android.library.flowlayout

import android.view.View

fun interface ViewAdapter<T : Any, V : View> {
    fun createViewFrom(data: T): V
}

fun <T : Any, V : View> FlowLayout.applyViewAdapter(
    adapter: ViewAdapter<T, V>,
    datas: List<T>,
    viewSetter: ((V, T) -> Unit)? = null
) {
    removeAllViews()
    for (data in datas) {
        val view = adapter.createViewFrom(data)
        addView(view)
        viewSetter?.invoke(view, data)
    }
    requestLayout()
}