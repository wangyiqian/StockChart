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
import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.entities.Highlight
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
    private var touchHelper = TouchHelper(this, TouchHelperCallBack())
    private var onKEntitiesChangedListeners = mutableSetOf<OnKEntitiesChangedListener>()
    private val matrixHelper = MatrixHelper(this)
    private var highlightMap = mutableMapOf<IChildChart, Highlight>()
    private var config: StockChartConfig =
        StockChartConfig()
    private val tmp2FloatArray = FloatArray(2)
    private val tmp4FloatArray = FloatArray(4)
    private val backgroundGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)

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

        if (config.appendKEntitiesFlag) {
            config.appendKEntitiesFlag = false
            onKEntitiesChangedListeners.forEach {
                it.onAppendKEntities()
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
        if(childCharts.isEmpty()) return null
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
            if (i in config.kEntities.indices && config.kEntities[i] !is EmptyKEntity) {
                result = i
                break
            }
        }
        return result
    }

    override fun findFirstNotEmptyKEntityIdxInDisplayArea(): Int? {
        if(childCharts.isEmpty()) return null
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
            if (i in config.kEntities.indices && config.kEntities[i] !is EmptyKEntity) {
                result = i
                break
            }
        }
        return result
    }

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
                        val layoutParams =
                            childCharts[index].view().layoutParams as LayoutParams
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        layoutParams.height = childChartConfig.height
                    }

                    if (childChartConfig.setMarginFlag) {
                        childChartConfig.setMarginFlag = false
                        needRequestLayout = true
                        val layoutParams =
                            childCharts[index].view().layoutParams as LayoutParams
                        layoutParams.leftMargin = 0
                        layoutParams.topMargin = childChartConfig.marginTop
                        layoutParams.rightMargin = 0
                        layoutParams.bottomMargin = childChartConfig.marginBottom
                    }
                }
            }
        }

        if (needReAddViews) {
            childCharts.clear()
            removeAllViews()
            config.childChartFactories.forEach {
                val childChart = it.createChart()
                childCharts.add(childChart)
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

        if (config.gridHorizontalLineCount > 0) {
            val space = height.toFloat() / (config.gridHorizontalLineCount + 1)
            var top = space

            for (i in 1..config.gridHorizontalLineCount) {
                canvas.drawLine(0f, top, width.toFloat(), top, backgroundGridPaint)
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
            requestDisallowInterceptTouchEvent(true)
            matrixHelper.handleTouchScaleBegin(focusX)
        }

        override fun onTouchScaling(scaleFactor: Float) {
            requestDisallowInterceptTouchEvent(true)
            matrixHelper.handleTouchScale(scaleFactor)
        }

        override fun onHScroll(distanceX: Float) {
            requestDisallowInterceptTouchEvent(true)
            matrixHelper.handleTouchScroll(distanceX)
        }

        override fun onTriggerFling(velocityX: Float, velocityY: Float) {
            matrixHelper.handleFlingStart(velocityX, velocityY)
        }

        override fun onLongPressMove(x: Float, y: Float) {
            requestDisallowInterceptTouchEvent(true)
            childCharts.forEach { childChart ->
                val childChartX = x - childChart.view().left
                val childChartY = y - childChart.view().top
                tmp2FloatArray[0] = childChartX
                tmp2FloatArray[1] = childChartY
                childChart.mapPointsReal2Value(tmp2FloatArray)
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

        override fun onTouchLeave() {
            highlightMap.keys.forEach {
                it.getConfig().onHighlightListener?.onHighlightEnd()
            }
            highlightMap.clear()
            notifyChanged()
            matrixHelper.checkScrollBack()
        }
    }

    override fun computeScroll() {
        matrixHelper.handleComputeScroll()
    }
}