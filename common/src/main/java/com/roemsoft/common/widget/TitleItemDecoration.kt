package com.roemsoft.common.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class TitleItemDecoration(private val itemTitleView: View, private val height: Int) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val spanCount = when (parent.layoutManager) {
            is GridLayoutManager -> (parent.layoutManager as GridLayoutManager).spanCount
            else -> 1
        }

        val toDrawWidthSpec: Int //用于测量的widthMeasureSpec

        //拿到复杂布局的LayoutParams，如果为空，就new一个。
        // 后面需要根据这个lp 构建toDrawWidthSpec，toDrawHeightSpec
        //拿到复杂布局的LayoutParams，如果为空，就new一个。
        // 后面需要根据这个lp 构建toDrawWidthSpec，toDrawHeightSpec
        var lp = itemTitleView.layoutParams
        if (lp == null) {
            lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ) //这里是根据复杂布局layout的width height，new一个Lp
            itemTitleView.layoutParams = lp
        }
        toDrawWidthSpec = when (lp.width) {
            ViewGroup.LayoutParams.MATCH_PARENT -> {    // 如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
                View.MeasureSpec.makeMeasureSpec(
                    (parent.width - parent.paddingLeft - parent.paddingRight) / spanCount,
                    View.MeasureSpec.EXACTLY
                )
            }
            ViewGroup.LayoutParams.WRAP_CONTENT -> {    // 如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
                View.MeasureSpec.makeMeasureSpec(
                    (parent.width - parent.paddingLeft - parent.paddingRight) / spanCount,
                    View.MeasureSpec.AT_MOST
                )
            }
            else -> {
                View.MeasureSpec.makeMeasureSpec(       // 否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
                    lp.width / spanCount,
                    View.MeasureSpec.EXACTLY
                )
            }
        }

        //高度同理
        //用于测量的heightMeasureSpec
        val toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上。
        itemTitleView.measure(toDrawWidthSpec, toDrawHeightSpec)
        for (position in 0 until spanCount) {
            itemTitleView.layout(
                parent.paddingLeft + itemTitleView.measuredWidth * position,
                parent.paddingTop,
                parent.paddingLeft + itemTitleView.measuredWidth * (position + 1),
                parent.paddingTop + itemTitleView.measuredHeight
            )
            parent.drawChild(c, itemTitleView, 0)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager is GridLayoutManager) {
            val layoutManager = parent.layoutManager as GridLayoutManager
            val spanCount = layoutManager.spanCount
            val position = parent.getChildAdapterPosition(view)
            if (position < spanCount) {
                outRect.top = height
            }
            return
        }

        if (parent.layoutManager is LinearLayoutManager) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = height
            }
            return
        }
    }
}