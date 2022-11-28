/*
 * Copyright 2021 WangYiqian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package com.github.wangyiqian.stockchart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import com.github.wangyiqian.stockchart.childchart.base.IChildChart
import com.github.wangyiqian.stockchart.entities.*
import com.github.wangyiqian.stockchart.listener.OnKEntitiesChangedListener
import com.github.wangyiqian.stockchart.util.checkMainThread
import kotlin.math.max
import kotlin.math.min

/**
 * 股票图，可包含K线图、成交量图、MACD图...
 * 子图目前只提供垂直线性布局
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
class StockChart @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewGroup(context, attrs),
    IStockChart {

    private val childCharts = mutableListOf<IChildChart>()
    private val touchHelper by lazy { TouchHelper(this, TouchHelperCallBack()) }
    private val onKEntitiesChangedListeners by lazy { mutableSetOf<OnKEntitiesChangedListener>() }
    private val matrixHelper by lazy { MatrixHelper(this) }
    private val highlightMap by lazy { mutableMapOf<IChildChart, Highlight>() }
    private var config: StockChartConfig =
        StockChartConfig()
    private val tmp2FloatArray by lazy { FloatArray(2) }
    private val tmp4FloatArray by lazy { FloatArray(4) }
    private val backgroundGridPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    init {
        setWillNotDraw(false)
        setOnTouchListener(touchHelper)
    }

    override fun getTouchArea() =
        Rect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)

    override fun addOnKEntitiesChangedListener(listener: OnKEntitiesChangedListener) {
        onKEntitiesChangedListeners.add(listener)
    }

    override fun removeOnKEntitiesChangedListener(listener: OnKEntitiesChangedListener) {
        onKEntitiesChangedListeners.remove(listener)
    }

    override fun getXScaleMatrix() = matrixHelper.xScaleMatrix

    override fun getFixXScaleMatrix() = matrixHelper.fixXScaleMatrix

    override fun getScrollMatrix() = matrixHelper.scrollMatrix

    override fun getHighlight(childChart: IChildChart) = highlightMap[childChart]

    override fun setConfig(config: StockChartConfig) {
        this.config = config
        notifyChanged()
    }

    override fun getConfig() = config

    override fun getChildCharts() = childCharts

    @UiThread
    override fun notifyChanged() {
        checkMainThread()
        if (config.setKEntitiesFlag) {
            config.setKEntitiesFlag = false
            matrixHelper.resetMatrix()
            onKEntitiesChangedListeners.forEach {
                it.onSetKEntities()
            }
        }

        if (config.modifyKEntitiesFlag) {
            config.modifyKEntitiesFlag = false
            onKEntitiesChangedListeners.forEach {
                it.onModifyKEntities()
            }
        }

        checkChildViews()

        invalidate()
        childCharts.forEach {
            it.invalidate()
        }
    }

    override fun dispatchOnLeftLoadMore() {
        config.getOnLoadMoreListeners().forEach {
            it.onLeftLoadMore()
        }
    }

    override fun dispatchOnRightLoadMore() {
        config.getOnLoadMoreListeners().forEach {
            it.onRightLoadMore()
        }
    }

    override fun findLastNotEmptyKEntityIdxInDisplayArea(): Int? {
        if (childCharts.isEmpty()) return null
        val chartDisplayArea = childCharts[0].getChartDisplayArea()
        tmp4FloatArray[0] = chartDisplayArea.left
        tmp4FloatArray[1] = 0f
        tmp4FloatArray[2] = chartDisplayArea.right
        tmp4FloatArray[3] = 0f
        childCharts[0].mapPointsReal2Value(tmp4FloatArray)
        val leftIdx = (tmp4FloatArray[0] + 0.5f).toInt()
        val rightIdx = (tmp4FloatArray[2] + 0.5f).toInt() - 1
        var result: Int? = null
        for (i in rightIdx downTo leftIdx) {
            if (i in config.kEntities.indices && !config.kEntities[i].containFlag(FLAG_EMPTY)) {
                result = i
                break
            }
        }
        return result
    }

    override fun findFirstNotEmptyKEntityIdxInDisplayArea(): Int? {
        if (childCharts.isEmpty()) return null
        val chartDisplayArea = childCharts[0].getChartDisplayArea()
        tmp4FloatArray[0] = chartDisplayArea.left
        tmp4FloatArray[1] = 0f
        tmp4FloatArray[2] = chartDisplayArea.right
        tmp4FloatArray[3] = 0f
        childCharts[0].mapPointsReal2Value(tmp4FloatArray)
        val leftIdx = (tmp4FloatArray[0] + 0.5f).toInt()
        val rightIdx = (tmp4FloatArray[2] + 0.5f).toInt() - 1
        var result: Int? = null
        for (i in leftIdx..rightIdx) {
            if (i in config.kEntities.indices && !config.kEntities[i].containFlag(FLAG_EMPTY)) {
                result = i
                break
            }
        }
        return result
    }

    override fun getTotalScaleX() = matrixHelper.getTotalScaleX()

    private fun checkChildViews() {
        var needReAddViews = false      // 是否需要重新添加view
        var needRequestLayout = false   // 是否需要重新requestLayout
        if (config.childChartFactories.size != childCharts.size) {
            needReAddViews = true
            needRequestLayout = true
        } else {
            run outSide@{
                config.childChartFactories.forEachIndexed { index, childChartFactory ->
                    val childChartConfig = childChartFactory.childChartConfig
                    if (childChartConfig != childCharts[index].getConfig()) {
                        needReAddViews = true
                        needRequestLayout = true
                        return@outSide
                    }
                    if (childChartConfig.setSizeFlag) {
                        childChartConfig.setSizeFlag = false
                        needRequestLayout = true
                        (childCharts[index].view().layoutParams as LayoutParams).apply {
                            width = ViewGroup.LayoutParams.MATCH_PARENT
                            height = childChartConfig.height
                        }
                    }

                    if (childChartConfig.setMarginFlag) {
                        childChartConfig.setMarginFlag = false
                        needRequestLayout = true
                        (childCharts[index].view().layoutParams as LayoutParams).apply {
                            leftMargin = 0
                            topMargin = childChartConfig.marginTop
                            rightMargin = 0
                            bottomMargin = childChartConfig.marginBottom
                        }
                    }
                }
            }
        }

        if (needReAddViews) {
            childCharts.clear()
            removeAllViews()
            config.childChartFactories.forEach {
                val childChart = it.createChart()
                childCharts += childChart
                addView(childChart.view())
            }
        }

        if (needRequestLayout) {
            requestLayout()
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var height = 0
        var width = 0
        childCharts.map { it.view() }.forEach { childView ->
            val childLayoutParams = childView.layoutParams as LayoutParams
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, height)
            height += childView.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin
            width = max(
                width,
                childView.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
            )
        }

        width += paddingLeft + paddingRight
        height += paddingTop + paddingBottom

        setMeasuredDimension(
            View.resolveSize(width, widthMeasureSpec),
            View.resolveSize(height, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childTop = paddingTop
        childCharts.map { it.view() }.forEach { childView ->
            val childMeasuredWidth = childView.measuredWidth
            val childMeasuredHeight = childView.measuredHeight
            val childLayoutParams = childView.layoutParams as LayoutParams
            val childLeft = paddingLeft + childLayoutParams.leftMargin
            childTop += childLayoutParams.topMargin

            val childRight = min(childLeft + childMeasuredWidth, measuredWidth - paddingRight)
            val childBottom = min(
                childTop + childMeasuredHeight,
                measuredHeight - paddingBottom
            )
            if (childRight > childLeft && childBottom > childTop) {
                childView.layout(childLeft, childTop, childRight, childBottom)
            }
            childTop = childBottom + childLayoutParams.bottomMargin
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawBackgroundColor(canvas)
        drawBackgroundGrid(canvas)
        super.onDraw(canvas)
    }

    private fun drawBackgroundColor(canvas: Canvas) {
        canvas.drawColor(config.backgroundColor)
    }

    private fun drawBackgroundGrid(canvas: Canvas) {
        backgroundGridPaint.color = config.gridLineColor
        backgroundGridPaint.strokeWidth = config.gridLineStrokeWidth
        backgroundGridPaint.pathEffect = config.gridLinePathEffect

        if (config.gridHorizontalLineCount > 0) {
            val space = config.horizontalGridLineSpaceCalculator?.invoke(this)
                ?: height.toFloat() / (config.gridHorizontalLineCount + 1)
            var top = config.horizontalGridLineTopOffsetCalculator?.invoke(this) ?: space

            for (i in 1..config.gridHorizontalLineCount) {
                canvas.drawLine(
                    config.horizontalGridLineLeftOffsetCalculator?.invoke(this) ?: 0f,
                    top,
                    width.toFloat(),
                    top,
                    backgroundGridPaint
                )
                top += space

            }
        }

        if (config.gridVerticalLineCount > 0) {
            val space = width.toFloat() / (config.gridVerticalLineCount + 1)
            var left = space
            for (i in 1..config.gridVerticalLineCount) {
                canvas.drawLine(left, 0f, left, height.toFloat(), backgroundGridPaint)
                left += space
            }
        }
    }

    class LayoutParams(width: Int, height: Int) : ViewGroup.MarginLayoutParams(width, height)

    inner class TouchHelperCallBack :
        TouchHelper.CallBack {

        override fun onTouchScaleBegin(focusX: Float) {
            if (getConfig().scaleAble) {
                requestDisallowInterceptTouchEvent(true)
                matrixHelper.handleTouchScaleBegin(focusX)
                getConfig().getOnGestureListeners().forEach { it.onScaleBegin(focusX) }
            }
        }

        override fun onTouchScaling(scaleFactor: Float) {
            if (getConfig().scaleAble) {
                requestDisallowInterceptTouchEvent(true)
                matrixHelper.handleTouchScale(scaleFactor)
                getConfig().getOnGestureListeners().forEach { it.onScaling(getTotalScaleX()) }
            }
        }

        override fun onHScroll(distanceX: Float) {
            if (getConfig().scrollAble) {
                requestDisallowInterceptTouchEvent(true)
                matrixHelper.handleTouchScroll(distanceX)
                getConfig().getOnGestureListeners().forEach { it.onHScrolling() }
            }
        }

        override fun onTriggerFling(velocityX: Float, velocityY: Float) {
            matrixHelper.handleFlingStart(velocityX, velocityY)
            getConfig().getOnGestureListeners().forEach { it.onFlingBegin() }
        }

        override fun onLongPressMove(x: Float, y: Float) {

            if (getConfig().showHighlightHorizontalLine
                || getConfig().showHighlightVerticalLine
                || childCharts.find { it.getConfig().onHighlightListener != null } != null
            ) {
                requestDisallowInterceptTouchEvent(true)
                childCharts.forEach { childChart ->
                    val childChartX = x - childChart.view().left
                    val childChartY = y - childChart.view().top
                    childChart.getHighlightValue(childChartX, childChartY, tmp2FloatArray)
                    val valueX = tmp2FloatArray[0]
                    val valueY = tmp2FloatArray[1]
                    var highlight = highlightMap[childChart]
                    if (highlight == null) {
                        highlight = Highlight(childChartX, childChartY, valueX, valueY)
                        highlightMap[childChart] = highlight
                        childChart.getConfig().onHighlightListener?.onHighlightBegin()

                    } else {
                        highlight.x = childChartX
                        highlight.y = childChartY
                        highlight.valueX = valueX
                        highlight.valueY = valueY
                    }
                    highlight?.apply { childChart.getConfig().onHighlightListener?.onHighlight(this) }
                }
                notifyChanged()
            }
        }

        override fun onTouchLeave() {
            highlightMap.keys.forEach {
                it.getConfig().onHighlightListener?.onHighlightEnd()
            }
            getConfig().getOnGestureListeners().forEach { it.onTouchLeave() }
            highlightMap.clear()
            notifyChanged()
            matrixHelper.checkScrollBack()

        }

        override fun onTap(x: Float, y: Float) {
            childCharts.forEach { childChart ->
                val childChartX = x - childChart.view().left
                val childChartY = y - childChart.view().top
                tmp2FloatArray[0] = childChartX
                tmp2FloatArray[1] = childChartY
                childChart.mapPointsReal2Value(tmp2FloatArray)
                val valueX = tmp2FloatArray[0]
                val valueY = tmp2FloatArray[1]
                val gestureEvent = GestureEvent(childChartX, childChartY, valueX, valueY)
                childChart.onTap(gestureEvent)
            }
            getConfig().getOnGestureListeners().forEach { it.onTap(x, y) }
        }

        override fun onLongPressBegin(x: Float, y: Float) {
            getConfig().getOnGestureListeners().forEach { it.onLongPressBegin(x, y) }
        }

        override fun onLongPressing(x: Float, y: Float) {
            getConfig().getOnGestureListeners().forEach { it.onLongPressing(x, y) }
        }

        override fun onLongPressEnd(x: Float, y: Float) {
            getConfig().getOnGestureListeners().forEach { it.onLongPressEnd(x, y) }
        }

    }

    override fun computeScroll() {
        matrixHelper.handleComputeScroll()
    }

}
