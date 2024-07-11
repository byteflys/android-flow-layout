package com.android.library.flowlayout

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.ViewGroup
import java.util.LinkedList

// A layout that enable auto-wrap on line end
class FlowLayout : ViewGroup {

    private var itemMarginX = 0
    private var itemMarginY = 0

    private val lock = Any()

    // location and bound of view
    private val xList = mutableListOf<Int>()
    private val yList = mutableListOf<Int>()
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

        //添加起始位置
        xList.clear()
        yList.clear()
        rects.clear()
        xList.add(paddingLeft)
        yList.add(paddingTop)

        //循环遍历子View，测量总尺寸
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            measureChild(child, wSpec, hSpec)
            val w = child.measuredWidth + lp.leftMargin + lp.rightMargin
            val h = child.measuredHeight + lp.topMargin + lp.bottomMargin
            val rect: Rect = findRect(w, h)
            rects.add(rect)
            insertValue(rect.left, xList)
            insertValue(rect.right, xList)
            insertValue(rect.top, yList)
            insertValue(rect.bottom, yList)
        }

        //确定最终ViewGroup高度
        var contentHeight = yList.last + paddingBottom
        if (modeH == MeasureSpec.AT_MOST && sizeH < contentHeight) contentHeight = sizeH
        super.setMeasuredDimension(sizeW, contentHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        //循环遍历子View，测量总尺寸
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            val rect: Rect? = rects[i]
            child.layout(rect.left + lp.leftMargin, rect.top + lp.topMargin, rect.right - lp.rightMargin, rect.bottom - lp.bottomMargin)
        }
    }

    //查找空白区域
    //尝试以每个边界点作为起点
    //如果和其它控件都不相交，也不超出parent宽度，则使用此边界点作为起点
    //否则切换到最底行最左侧显示
    protected fun findRect(w: Int, h: Int): Rect {
        synchronized(lock) {
            for (x1 in xList) for (i in 0 until yList.size - 1) {
                val y1 = yList[i]
                val x2 = x1 + w
                val y2 = y1 + h
                //判断是否相交
                val rect: Rect = Rect(x1, y1, x2, y2)
                var cross = false
                breakPoint@ for (usedRect in rects) if (Rect.isCrossed(rect, usedRect)) {
                    cross = true
                    break@breakPoint
                }
                //不相交，不超出parent宽度，则可以放置在此位置
                if (!cross) if (x2 <= parentWidth - paddingRight + Dimens.toPx(1)) //防止float和int转换时精度丢失，条件放宽松点
                    return rect
            }
            //其它点都不合适，切换到最底部显示
            return Rect(paddingLeft, yList.last, paddingLeft + w, yList.last + h)
        }
    }

    //记录临界点
    protected fun insertValue(value: Int, intList: LinkedList<Int>) {
        var index = -1
        for (i in intList.size downTo 1) {
            val item = intList[i - 1]
            if (item == value) break
            if (item < value) {
                index = i
                break
            }
        }
        if (index >= 0) intList.add(index, value)
    }
}


