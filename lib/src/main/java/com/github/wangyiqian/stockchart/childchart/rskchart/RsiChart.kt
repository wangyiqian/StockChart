package com.github.wangyiqian.stockchart.childchart.rskchart

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChart
import com.github.wangyiqian.stockchart.entities.GestureEvent
import kotlin.math.abs

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2023/3/9
 */
class RsiChart(stockChart: IStockChart, chartConfig: RsiChartConfig) :
    BaseChildChart<RsiChartConfig>(stockChart, chartConfig) {
    private val linePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
        }
    }
    private val dashLinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            pathEffect = DashPathEffect(floatArrayOf(10f, 8f), 0f)
        }
    }
    private val indexTextPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightHorizontalLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightVerticalLinePaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    private var indexList: List<List<Float?>>? = null
    private var drawnIndexTextHeight = 0f

    private val indexStarterTextBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val indexStarterRightIconPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var indexStarterRectF = RectF()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        indexList = chartConfig.index?.calculate(getKEntities())
    }

    override fun onKEntitiesChanged() {
        indexList = chartConfig.index?.calculate(getKEntities())
    }

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
        var yMax = 0f
        var yMin = 0f
        var firstCalc = true
        indexList?.forEach { valueList ->
            valueList.filterIndexed { idx, _ -> idx in startIndex..endIndex }.filterNotNull()
                .apply {
                    if (size > 0) {
                        if (firstCalc) {
                            firstCalc = false
                            yMax = this[0]
                            yMin = this[0]
                        }
                        yMax = kotlin.math.max(yMax, max()!!)
                        yMin = kotlin.math.min(yMin, min()!!)
                    }
                }
        }

        if (abs(yMin - yMax) > 0.0001) {
            result[0] = yMin
            result[1] = yMax
        } else { // 约等于0
            var delta = 2
            result[0] = yMin - delta
            result[1] = yMax + delta
        }
    }

    override fun preDrawData(canvas: Canvas) {
        dashLinePaint.color = chartConfig.dashLineColor
        val centerY = (getChartDisplayArea().top + getChartDisplayArea().bottom) / 2
        canvas.drawLine(
            getChartDisplayArea().left,
            centerY,
            getChartDisplayArea().right,
            centerY,
            dashLinePaint
        )
    }

    override fun onTap(event: GestureEvent) {
        super.onTap(event)
        if (indexStarterRectF.width() == 0f) return
        val extSpace = 20f
        if (event.x > indexStarterRectF.left - extSpace && event.x < indexStarterRectF.right + extSpace && event.y > indexStarterRectF.top - extSpace && event.y < indexStarterRectF.bottom + extSpace) {
            chartConfig.indexStarterClickListener?.invoke(this)
        }
    }

    override fun drawData(canvas: Canvas) {
        drawnIndexTextHeight = 0f
        if (chartConfig.index == null) {
            return
        }
        linePaint.strokeWidth = chartConfig.lineStrokeWidth
        indexList?.forEachIndexed { lineIdx, pointList ->
            chartConfig.indexColors?.let { indexColors ->
                if (lineIdx < indexColors.size) {
                    linePaint.color = indexColors[lineIdx]
                    var preIdx = -1
                    pointList.forEachIndexed { pointIdx, point ->
                        if (point == null) {
                            preIdx = -1
                            return@forEachIndexed
                        }

                        if (preIdx == -1) {
                            preIdx = pointIdx
                            return@forEachIndexed
                        }

                        tmp4FloatArray[0] = preIdx + 0.5f
                        tmp4FloatArray[1] = pointList[preIdx]!!
                        tmp4FloatArray[2] = pointIdx + 0.5f
                        tmp4FloatArray[3] = pointList[pointIdx]!!
                        mapPointsValue2Real(tmp4FloatArray)
                        canvas.drawLine(
                            tmp4FloatArray[0],
                            tmp4FloatArray[1],
                            tmp4FloatArray[2],
                            tmp4FloatArray[3],
                            linePaint
                        )
                        preIdx = pointIdx
                    }
                }
            }
        }

        // draw index text
        chartConfig.index?.let { index ->
            indexList?.let { indexList ->
                val highlight = getHighlight()
                var indexIdx =
                    highlight?.getIdx() ?: stockChart.findLastNotEmptyKEntityIdxInDisplayArea()
                indexTextPaint.textSize = index.textSize
                var left = index.textMarginLeft
                var top = index.textMarginTop
                indexTextPaint.getFontMetrics(tmpFontMetrics)
                val textHeight = tmpFontMetrics.bottom - tmpFontMetrics.top
                if (!index.startText.isNullOrEmpty()) {
                    indexTextPaint.color = index.startTextColor
                    indexStarterTextBgPaint.color = chartConfig.indexStarterBgColor
                    val textWidth = indexTextPaint.measureText(index.startText)
                    val textLeft = left + chartConfig.indexStarterBgPaddingHorizontal
                    var width = textWidth + chartConfig.indexStarterBgPaddingHorizontal * 2
                    val height = textHeight + top
                    val indexStarterRightIconMarginLeft = 10
                    chartConfig.indexStarterRightIcon?.also {
                        width += it.width + indexStarterRightIconMarginLeft
                    }
                    canvas.drawRoundRect(
                        left,
                        top,
                        left + width,
                        top + height,
                        5f,
                        5f,
                        indexStarterTextBgPaint
                    )
                    indexStarterRectF.set(left, top, left + width, top + height)
                    chartConfig.indexStarterRightIcon?.also {
                        canvas.drawBitmap(
                            it,
                            textLeft + textWidth + indexStarterRightIconMarginLeft,
                            top + (height - it.height) / 2,
                            indexStarterRightIconPaint
                        )
                    }
                    canvas.drawText(
                        index.startText,
                        textLeft,
                        -tmpFontMetrics.top + top,
                        indexTextPaint
                    )
                    left += width + index.textSpace
                    drawnIndexTextHeight = height
                } else {
                    indexStarterRectF.set(0f, 0f, 0f, 0f)
                }
                var isFirstLine = true
                indexList.forEachIndexed { lineIdx, pointList ->
                    chartConfig.indexColors?.let { indexColors ->
                        if (lineIdx < indexColors.size) {
                            indexTextPaint.color = indexColors[lineIdx]
                            val value =
                                if (indexIdx != null && indexIdx in pointList.indices && pointList[indexIdx] != null) pointList[indexIdx] else null
                            val text = index.textFormatter.invoke(lineIdx, value)
                            val textWidth = indexTextPaint.measureText(text)

                            if (left + textWidth > getChartDisplayArea().width()) {
                                // 需要换行
                                isFirstLine = false
                                left = index.textMarginLeft
                                top += textHeight
                                drawnIndexTextHeight += textHeight
                            }

                            if (isFirstLine) {
                                drawnIndexTextHeight = textHeight + index.textMarginTop
                            }

                            canvas.drawText(
                                text,
                                left,
                                -tmpFontMetrics.top + top,
                                indexTextPaint
                            )
                            left += indexTextPaint.measureText(text) + index.textSpace
                        }
                    }
                }
            }
        }
    }

    override fun drawHighlight(canvas: Canvas) {
        getHighlight()?.let { highlight ->

            var highlightY = highlight.y
            var highlightX = highlight.x
            var highlightValueY = highlight.valueY

            val highlightAreaTop = getChartDisplayArea().top + drawnIndexTextHeight
            if (stockChart.getConfig().showHighlightHorizontalLine) {
                if (highlightY >= highlightAreaTop && highlightY <= getChartDisplayArea().bottom) {

                    highlightHorizontalLinePaint.color =
                        stockChart.getConfig().highlightHorizontalLineColor
                    highlightHorizontalLinePaint.strokeWidth =
                        stockChart.getConfig().highlightHorizontalLineWidth

                    var highlightHorizontalLineLeft = getChartDisplayArea().left
                    var highlightHorizontalLineRight = getChartDisplayArea().right

                    // left highlight label
                    chartConfig.highlightLabelLeft?.let { highlightLabel ->
                        highlightLabelPaint.textSize = highlightLabel.textSize
                        highlightLabelPaint.color = highlightLabel.textColor
                        highlightLabelBgPaint.color = highlightLabel.bgColor
                        val text = highlightLabel.textFormat(highlightValueY)
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = getChartDisplayArea().left
                        tmpRectF.top = highlightY - bgHeight / 2
                        tmpRectF.right = bgWidth
                        tmpRectF.bottom = highlightY + bgHeight / 2
                        if (tmpRectF.top < highlightAreaTop) {
                            tmpRectF.offset(0f, highlightAreaTop - tmpRectF.top)
                        } else if (tmpRectF.bottom > getChartDisplayArea().bottom) {
                            tmpRectF.offset(0f, getChartDisplayArea().bottom - tmpRectF.bottom)
                        }
                        highlightLabelPaint.getFontMetrics(tmpFontMetrics)
                        val textBaseLine =
                            tmpRectF.top + bgHeight / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

                        canvas.drawRoundRect(
                            tmpRectF,
                            highlightLabel.bgCorner,
                            highlightLabel.bgCorner,
                            highlightLabelBgPaint
                        )

                        canvas.drawText(
                            text,
                            tmpRectF.left + highlightLabel.padding,
                            textBaseLine,
                            highlightLabelPaint
                        )

                        highlightHorizontalLineLeft += bgWidth
                    }

                    // right highlight label
                    chartConfig.highlightLabelRight?.let { highlightLabel ->
                        highlightLabelPaint.textSize = highlightLabel.textSize
                        highlightLabelPaint.color = highlightLabel.textColor
                        highlightLabelBgPaint.color = highlightLabel.bgColor
                        val text = highlightLabel.textFormat(highlightValueY)
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = getChartDisplayArea().right - bgWidth
                        tmpRectF.top = highlightY - bgHeight / 2
                        tmpRectF.right = getChartDisplayArea().right
                        tmpRectF.bottom = highlightY + bgHeight / 2
                        if (tmpRectF.top < highlightAreaTop) {
                            tmpRectF.offset(0f, highlightAreaTop - tmpRectF.top)
                        } else if (tmpRectF.bottom > getChartDisplayArea().bottom) {
                            tmpRectF.offset(0f, getChartDisplayArea().bottom - tmpRectF.bottom)
                        }
                        highlightLabelPaint.getFontMetrics(tmpFontMetrics)
                        val textBaseLine =
                            tmpRectF.top + bgHeight / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

                        canvas.drawRoundRect(
                            tmpRectF,
                            highlightLabel.bgCorner,
                            highlightLabel.bgCorner,
                            highlightLabelBgPaint
                        )

                        canvas.drawText(
                            text,
                            tmpRectF.left + highlightLabel.padding,
                            textBaseLine,
                            highlightLabelPaint
                        )

                        highlightHorizontalLineRight -= bgWidth
                    }

                    // highlight horizontal line
                    canvas.drawLine(
                        highlightHorizontalLineLeft,
                        highlightY,
                        highlightHorizontalLineRight,
                        highlightY,
                        highlightHorizontalLinePaint
                    )
                }
            }

            if (stockChart.getConfig().showHighlightVerticalLine) {
                if (highlightX >= getChartDisplayArea().left && highlightX <= getChartDisplayArea().right) {

                    highlightVerticalLinePaint.color =
                        stockChart.getConfig().highlightVerticalLineColor
                    highlightVerticalLinePaint.strokeWidth =
                        stockChart.getConfig().highlightVerticalLineWidth

                    tmp2FloatArray[0] = highlight.getIdx() + 0.5f
                    tmp2FloatArray[1] = 0f
                    mapPointsValue2Real(tmp2FloatArray)
                    val x = tmp2FloatArray[0]

                    // highlight vertical line
                    canvas.drawLine(
                        x,
                        highlightAreaTop,
                        x,
                        getChartDisplayArea().bottom,
                        highlightVerticalLinePaint
                    )
                }
            }
        }
    }

    override fun drawAddition(canvas: Canvas) {}
    override fun drawBackground(canvas: Canvas) {
    }

    override fun preDrawBackground(canvas: Canvas) {}
    override fun preDrawHighlight(canvas: Canvas) {}
}