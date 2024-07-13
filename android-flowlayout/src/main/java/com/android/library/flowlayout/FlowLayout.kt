package com.android.library.flowlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import com.android.library.flowlayout.commons.android.Views.layout
import com.android.library.flowlayout.commons.kotlin.Maths

// A layout that enable auto-wrap on line end
class FlowLayout : ViewGroup {

    private var itemMarginX = 0
    private var itemMarginY = 0

    // all cross points
    private val xList = mutableListOf<Int>()
    private val yList = mutableListOf<Int>()
    // bound of measured view
    private val rects = mutableListOf<Rect>()

    private var parentWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout)
        itemMarginX = typedArray.getDimension(R.styleable.FlowLayout_itemMarginX, 0f).toInt()
        itemMarginY = typedArray.getDimension(R.styleable.FlowLayout_itemMarginY, 0f).toInt()
        typedArray.recycle()
    }

    override fun onMeasure(wSpec: Int, hSpec: Int) {

        // measure spec for layout
        val modeW = MeasureSpec.getMode(wSpec)
        val modeH = MeasureSpec.getMode(hSpec)
        val sizeW = MeasureSpec.getSize(wSpec)
        val sizeH = MeasureSpec.getSize(hSpec)

        // flow layout must have a definite width
        // in order to decide where to wrap line
        if (modeW != MeasureSpec.EXACTLY)
            throw RuntimeException("flow layout must have an exactly width, please use fixed width or match_parent")
        parentWidth = sizeW

        // add start location
        xList.clear()
        yList.clear()
        rects.clear()
        xList.add(itemMarginX + paddingLeft)
        yList.add(itemMarginY + paddingTop)

        // measure child size and location
        var minY = itemMarginY
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, wSpec, hSpec)
            val w = child.measuredWidth
            val h = child.measuredHeight
            val rect = findLocateBound(w, h, sizeW, minY)
            rects.add(rect)
            minY = rect.top
            saveMeasuredRect(rect)
        }

        // measure layout height
        var contentHeight = sizeH
        if (modeH == MeasureSpec.EXACTLY)
            contentHeight = sizeH
        if (modeH == MeasureSpec.UNSPECIFIED)
            contentHeight = yList.last() + paddingBottom
        if (modeH == MeasureSpec.AT_MOST)
            contentHeight = (yList.last() + paddingBottom).coerceAtMost(sizeH)
        super.setMeasuredDimension(sizeW, contentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val rect = rects[i]
            child.layout(rect)
        }
    }

    // find where to place new item
    // try with each cross point one by one, as the start point
    // if not crossed with other items, and width is inside parent, it is ok
    // else break to new line
    private fun findLocateBound(w: Int, h: Int, maxWidth: Int, minY: Int): Rect {
        for (y in yList)
            if (y >= minY)
                for (x in xList)
                    if (x + w <= maxWidth)
                        if (!intersect(x, y, w, h))
                            return Rect(x, y, x + w, y + h)
        val startX = itemMarginX + paddingLeft
        val startY = yList.last() + itemMarginY
        return Rect(startX, startY, startX + w, startY + h)
    }

    // intersect with other items
    private fun intersect(x: Int, y: Int, w: Int, h: Int): Boolean {
        val rect = Rect(x, y, x + w, y + h)
        for (usedRect in rects)
            if (rect.intersect(usedRect))
                return true
        return false
    }

    private fun saveMeasuredRect(rect: Rect) {
        saveCrossCoordinate(rect.left, xList)
        saveCrossCoordinate(rect.right + itemMarginX, xList)
        saveCrossCoordinate(rect.top, yList)
        saveCrossCoordinate(rect.bottom + itemMarginY, yList)
    }

    // save new cross point coordinate
    // if exists, skip save
    private fun saveCrossCoordinate(value: Int, list: MutableList<Int>) {
        var index = Maths.findInsertIndexAsc(list, value)
        if (index >= 0)
            list.add(index, value)
    }
}