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

package com.github.wangyiqian.stockchart.childchart.kchart

import android.graphics.*
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChart
import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.entities.KEntityOfLineStarter
import com.github.wangyiqian.stockchart.index.Index
import kotlin.math.max
import kotlin.math.min

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
open class KChart(
    stockChart: IStockChart,
    chartConfig: KChartConfig
) : BaseChildChart<KChartConfig>(stockChart, chartConfig) {

    private val lineKChartLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val candleKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val hollowKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val barKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val mountainKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val mountainGradientKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
    private var mountainLinearGradient: LinearGradient? = null
    private var mountainLinearGradientColors = intArrayOf()
    private val highlightHorizontalLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightVerticalLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val costPriceLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightLabelBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val indexPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }
    private val indexTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highestAndLowestLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val avgPriceLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    private var indexList: List<List<Float?>>? = null
    private var lastCalculateIndexType: Index? = null

    private var drawnIndexTextHeight = 0f

    override fun onKEntitiesChanged() {
        calculateIndexList()
    }

    private fun calculateIndexList() {
        indexList = null
        lastCalculateIndexType = chartConfig.index
        chartConfig.index?.apply {
            when (this) {
                is Index.MA, is Index.EMA, is Index.BOLL -> {
                    indexList = calculate(getKEntities())
                }
                else -> {
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setMountainLinearGradient()
    }

    private fun setMountainLinearGradient() {
        mountainLinearGradientColors = chartConfig.mountainChartLinearGradientColors
        mountainLinearGradient = LinearGradient(
            0f,
            getChartDisplayArea().top,
            0f,
            getChartDisplayArea().bottom,
            mountainLinearGradientColors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {

        if (chartConfig.index == null ||  chartConfig.index != lastCalculateIndexType) {
            calculateIndexList()
        }

        var yMin = 0f
        var yMax = 0f

        getKEntities().filterIndexed { index, kEntity -> index in startIndex..endIndex && kEntity !is EmptyKEntity }
            .apply {
                when (chartConfig.kChartType) {
                    is KChartConfig.KChartType.CANDLE, is KChartConfig.KChartType.HOLLOW, is KChartConfig.KChartType.BAR -> {
                        yMin = minBy { it.getLowPrice() }?.getLowPrice() ?: 0f
                        yMax = maxBy { it.getHighPrice() }?.getHighPrice() ?: 0f
                    }
                    else -> {
                        forEachIndexed { index, kEntity ->
                            if (index == 0) {
                                yMin = kEntity.getClosePrice()
                                yMax = kEntity.getClosePrice()
                            } else {
                                yMin = min(yMin, kEntity.getClosePrice())
                                yMax = max(yMax, kEntity.getClosePrice())
                            }
                            kEntity.getAvgPrice()?.let { avgPrice ->
                                if (chartConfig.showAvgLine) {
                                    yMin = min(yMin, avgPrice)
                                    yMax = max(yMax, avgPrice)
                                }
                            }
                        }
                    }
                }
            }

        indexList?.forEach { valueList ->
            valueList.filterIndexed { idx, _ -> idx in startIndex..endIndex }.filterNotNull()
                .apply {
                    if (size > 0) {
                        yMax = max(yMax, max()!!)
                        yMin = min(yMin, min()!!)
                    }
                }
        }

        result[0] = yMin
        result[1] = yMax
    }

    override fun preDrawBackground(canvas: Canvas) {}

    override fun drawBackground(canvas: Canvas) {}

    override fun preDrawData(canvas: Canvas) {}

    override fun drawData(canvas: Canvas) {
        when (chartConfig.kChartType) {
            is KChartConfig.KChartType.LINE -> {
                drawLineKChart(canvas)
            }
            is KChartConfig.KChartType.CANDLE -> {
                drawCandleKChart(canvas)
            }
            is KChartConfig.KChartType.HOLLOW -> {
                drawHollowKChart(canvas)
            }
            is KChartConfig.KChartType.MOUNTAIN -> {
                drawMountainKChart(canvas)
            }
            is KChartConfig.KChartType.BAR -> {
                drawBarKChart(canvas)
            }
        }
        drawHighestAndLowestLabel(canvas)
        drawIndex(canvas)
    }

    override fun preDrawHighlight(canvas: Canvas) {
        drawCostPriceLine(canvas)
        drawLabels(canvas)
    }

    override fun drawHighlight(canvas: Canvas) {
        getHighlight()?.let { highlight ->
            val highlightAreaTop = getChartDisplayArea().top + drawnIndexTextHeight
            if (stockChart.getConfig().showHighlightHorizontalLine) {
                if (highlight.y >= highlightAreaTop && highlight.y <= getChartDisplayArea().bottom) {

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
                        val text = highlightLabel.textFormat(highlight.valueY)
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = getChartDisplayArea().left
                        tmpRectF.top = highlight.y - bgHeight / 2
                        tmpRectF.right = bgWidth
                        tmpRectF.bottom = highlight.y + bgHeight / 2
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
                        val text = highlightLabel.textFormat(highlight.valueY)
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = getChartDisplayArea().right - bgWidth
                        tmpRectF.top = highlight.y - bgHeight / 2
                        tmpRectF.right = getChartDisplayArea().right
                        tmpRectF.bottom = highlight.y + bgHeight / 2
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
                        highlight.y,
                        highlightHorizontalLineRight,
                        highlight.y,
                        highlightHorizontalLinePaint
                    )
                }
            }

            if (stockChart.getConfig().showHighlightVerticalLine) {
                if (highlight.x >= getChartDisplayArea().left && highlight.x <= getChartDisplayArea().right) {

                    highlightVerticalLinePaint.color =
                        stockChart.getConfig().highlightVerticalLineColor
                    highlightVerticalLinePaint.strokeWidth =
                        stockChart.getConfig().highlightVerticalLineWidth

                    tmp2FloatArray[0] = highlight.getIdx() + 0.5f
                    tmp2FloatArray[1] = 0f
                    mapPointsValue2Real(tmp2FloatArray)
                    val x = tmp2FloatArray[0]

                    var highlightHorizontalLineTop = highlightAreaTop
                    var highlightHorizontalLineBottom = getChartDisplayArea().bottom

                    // top highlight label
                    chartConfig.highlightLabelTop?.let { highlightLabel ->
                        highlightLabelPaint.textSize = highlightLabel.textSize
                        highlightLabelPaint.color = highlightLabel.textColor
                        highlightLabelBgPaint.color = highlightLabel.bgColor
                        val text = highlightLabel.textFormat(highlight.getIdx().toFloat())
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = x - bgWidth / 2
                        tmpRectF.top = highlightAreaTop
                        tmpRectF.right = x + bgWidth / 2
                        tmpRectF.bottom = highlightAreaTop + bgHeight
                        if (tmpRectF.left < getChartDisplayArea().left) {
                            tmpRectF.offset(getChartDisplayArea().left - tmpRectF.left, 0f)
                        } else if (tmpRectF.right > getChartDisplayArea().right) {
                            tmpRectF.offset(getChartDisplayArea().right - tmpRectF.right, 0f)
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

                        highlightHorizontalLineTop += bgHeight
                    }

                    // bottom highlight label
                    chartConfig.highlightLabelBottom?.let { highlightLabel ->
                        highlightLabelPaint.textSize = highlightLabel.textSize
                        highlightLabelPaint.color = highlightLabel.textColor
                        highlightLabelBgPaint.color = highlightLabel.bgColor
                        val text = highlightLabel.textFormat(highlight.getIdx().toFloat())
                        highlightLabelPaint.getTextBounds(text, 0, text.length, tmpRect)
                        val textWidth = tmpRect.width()
                        val textHeight = tmpRect.height()
                        val bgWidth = textWidth + highlightLabel.padding * 2
                        val bgHeight = textHeight + highlightLabel.padding * 2
                        tmpRectF.left = x - bgWidth / 2
                        tmpRectF.top = getChartDisplayArea().bottom - bgHeight
                        tmpRectF.right = x + bgWidth / 2
                        tmpRectF.bottom = getChartDisplayArea().bottom
                        if (tmpRectF.left < getChartDisplayArea().left) {
                            tmpRectF.offset(getChartDisplayArea().left - tmpRectF.left, 0f)
                        } else if (tmpRectF.right > getChartDisplayArea().right) {
                            tmpRectF.offset(getChartDisplayArea().right - tmpRectF.right, 0f)
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

                        highlightHorizontalLineBottom -= bgHeight
                    }

                    // highlight vertical line
                    canvas.drawLine(
                        x,
                        highlightHorizontalLineTop,
                        x,
                        highlightHorizontalLineBottom,
                        highlightVerticalLinePaint
                    )
                }
            }
        }
    }

    override fun drawAddition(canvas: Canvas) {}

    private fun drawCostPriceLine(canvas: Canvas) {
        chartConfig.costPrice?.let {
            costPriceLinePaint.color = chartConfig.costPriceLineColor
            costPriceLinePaint.strokeWidth = chartConfig.costPriceLineWidth
            tmp2FloatArray[0] = 0f
            tmp2FloatArray[1] = it
            mapPointsValue2Real(tmp2FloatArray)
            canvas.drawLine(
                getChartDisplayArea().left,
                tmp2FloatArray[1],
                getChartDisplayArea().right,
                tmp2FloatArray[1],
                costPriceLinePaint
            )
        }
    }

    private fun drawHighestAndLowestLabel(canvas: Canvas) {
        chartConfig.kChartType.highestAndLowestLabelConfig?.let { config ->
            highestAndLowestLabelPaint.textSize = config.labelTextSize
            highestAndLowestLabelPaint.strokeWidth = config.lineStrokeWidth
            highestAndLowestLabelPaint.color = config.labelColor
            tmp4FloatArray[0] = getChartMainDisplayArea().left
            tmp4FloatArray[1] = 0f
            tmp4FloatArray[2] = getChartMainDisplayArea().right
            tmp4FloatArray[3] = 0f
            mapPointsReal2Value(tmp4FloatArray)
            val leftIdx = (tmp4FloatArray[0] + 0.5f).toInt()
            val rightIdx = (tmp4FloatArray[2] + 0.5f).toInt() - 1
            getKEntities().filterIndexed { kEntityIdx, kEntity -> kEntityIdx in leftIdx..rightIdx && kEntity !is EmptyKEntity }
                .map { it.getHighPrice() }.max()

            var maxIdx: Int? = null
            var minIdx: Int? = null
            var maxPrice = 0f
            var minPrice = 0f
            val kEntities = getKEntities()
            for (i in leftIdx..rightIdx) {
                if (i in kEntities.indices && kEntities[i] !is EmptyKEntity) {
                    if (minIdx == null || maxIdx == null) {
                        maxIdx = i
                        minIdx = i
                        maxPrice = kEntities[i].getHighPrice()
                        minPrice = kEntities[i].getLowPrice()
                    } else {
                        if (kEntities[i].getHighPrice() > maxPrice) {
                            maxIdx = i
                            maxPrice = kEntities[i].getHighPrice()
                        }
                        if (kEntities[i].getLowPrice() < minPrice) {
                            minIdx = i
                            minPrice = kEntities[i].getLowPrice()
                        }
                    }
                }
            }

            fun doDraw(idx: Int, price: Float) {
                tmp2FloatArray[0] = idx + 0.5f
                tmp2FloatArray[1] = price
                mapPointsValue2Real(tmp2FloatArray)
                val isLeft =
                    tmp2FloatArray[0] - getChartDisplayArea().left > (getChartDisplayArea().right - getChartDisplayArea().left) / 2
                val lineLength = config.lineLength
                val lineEndX =
                    if (isLeft) tmp2FloatArray[0] - lineLength else tmp2FloatArray[0] + lineLength
                canvas.drawLine(
                    tmp2FloatArray[0],
                    tmp2FloatArray[1],
                    lineEndX,
                    tmp2FloatArray[1],
                    highestAndLowestLabelPaint
                )
                val text = "${config.formatter.invoke(price)}"
                val textWidth = highestAndLowestLabelPaint.measureText(text)
                val textStartX = if (isLeft) lineEndX - textWidth else lineEndX
                highestAndLowestLabelPaint.getFontMetrics(tmpFontMetrics)
                val baseLine =
                    tmp2FloatArray[1] + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
                canvas.drawText(text, textStartX, baseLine, highestAndLowestLabelPaint)
            }

            maxIdx?.let {
                doDraw(it, maxPrice)
            }

            minIdx?.let {
                doDraw(it, minPrice)
            }
        }
    }

    private fun drawIndex(canvas: Canvas) {
        drawnIndexTextHeight = 0f
        if (chartConfig.index == null) {
            return
        }
        indexPaint.strokeWidth = chartConfig.indexStrokeWidth
        indexList?.forEachIndexed { lineIdx, pointList ->
            chartConfig.indexColors?.let { indexColors ->
                if (lineIdx < indexColors.size) {
                    indexPaint.color = indexColors[lineIdx]
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
                            indexPaint
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
                val top = index.textMarginTop
                indexTextPaint.getFontMetrics(tmpFontMetrics)
                fun drawIndexText(text: String) {
                    canvas.drawText(
                        text,
                        left,
                        -tmpFontMetrics.top + top,
                        indexTextPaint
                    )
                    left += indexTextPaint.measureText(text) + index.textSpace
                    drawnIndexTextHeight =
                        tmpFontMetrics.bottom - tmpFontMetrics.top
                }
                if (!index.startText.isNullOrEmpty()) {
                    indexTextPaint.color = index.startTextColor
                    drawIndexText(index.startText)
                }
                indexList.forEachIndexed { lineIdx, pointList ->
                    chartConfig.indexColors?.let { indexColors ->
                        if (lineIdx < indexColors.size) {
                            indexTextPaint.color = indexColors[lineIdx]
                            val value =
                                if (indexIdx != null && indexIdx in pointList.indices && pointList[indexIdx] != null) pointList[indexIdx] else null
                            val text = index.textFormatter.invoke(lineIdx, value)
                            drawIndexText(text)
                        }
                    }
                }
            }
        }
    }

    private fun drawMountainKChart(canvas: Canvas) {
        mountainKChartPaint.strokeWidth = chartConfig.mountainChartStrokeWidth
        mountainKChartPaint.color = chartConfig.mountainChartColor

        if (!mountainLinearGradientColors.contentEquals(chartConfig.mountainChartLinearGradientColors)) {
            setMountainLinearGradient()
        }

        mountainGradientKChartPaint.shader = mountainLinearGradient
        tmpPath.reset()

        var preIdx = -1
        for (idx in getKEntities().indices) {
            if (getKEntities()[idx] is EmptyKEntity || getKEntities()[idx] is KEntityOfLineStarter) {
                if (preIdx != -1) {
                    tmpPath.lineTo(preIdx + 0.5f, 0f)
                    mapPathValue2Real(tmpPath)
                    canvas.drawPath(tmpPath, mountainGradientKChartPaint)
                    tmpPath.reset()
                }
                preIdx = -1
                if (getKEntities()[idx] is EmptyKEntity) {
                    continue
                }
            }
            if (preIdx == -1) {
                preIdx = idx
                tmpPath.reset()
                tmpPath.moveTo(preIdx + 0.5f, 0f)
            } else {
                preIdx = idx
            }

            tmpPath.lineTo(idx + 0.5f, getKEntities()[idx].getClosePrice())
        }

        if (preIdx != -1) {
            tmpPath.lineTo(preIdx + 0.5f, 0f)
            mapPathValue2Real(tmpPath)
            canvas.drawPath(tmpPath, mountainGradientKChartPaint)
            tmpPath.reset()
        }

        preIdx = -1
        for (idx in getKEntities().indices) {
            if (getKEntities()[idx] is EmptyKEntity) {
                preIdx = -1
                continue
            }

            if (preIdx == -1 || getKEntities()[idx] is KEntityOfLineStarter) {
                preIdx = idx
                continue
            }

            tmp4FloatArray[0] = preIdx + 0.5f
            tmp4FloatArray[1] = getKEntities()[preIdx].getClosePrice()
            tmp4FloatArray[2] = idx + 0.5f
            tmp4FloatArray[3] = getKEntities()[idx].getClosePrice()
            mapPointsValue2Real(tmp4FloatArray)
            canvas.drawLine(
                tmp4FloatArray[0],
                tmp4FloatArray[1],
                tmp4FloatArray[2],
                tmp4FloatArray[3],
                mountainKChartPaint
            )
            preIdx = idx
        }
    }

    private fun drawBarKChart(canvas: Canvas) {
        barKChartPaint.strokeWidth = chartConfig.barChartLineStrokeWidth
        val barWidth = 1 * (1 - chartConfig.barSpaceRatio)
        val spaceWidth = 1 * chartConfig.barSpaceRatio
        var left = spaceWidth / 2f
        getKEntities().forEach { kEntity ->
            if (kEntity !is EmptyKEntity) {
                barKChartPaint.color =
                    if (kEntity.getClosePrice() >= kEntity.getOpenPrice()) stockChart.getConfig().riseColor else stockChart.getConfig().downColor

                tmp12FloatArray[0] = left + barWidth / 2
                tmp12FloatArray[1] = kEntity.getHighPrice()
                tmp12FloatArray[2] = left + barWidth / 2
                tmp12FloatArray[3] = kEntity.getLowPrice()

                tmp12FloatArray[4] = left
                tmp12FloatArray[5] = kEntity.getOpenPrice()
                tmp12FloatArray[6] = left + barWidth / 2
                tmp12FloatArray[7] = kEntity.getOpenPrice()

                tmp12FloatArray[8] = left + barWidth / 2
                tmp12FloatArray[9] = kEntity.getClosePrice()
                tmp12FloatArray[10] = left + barWidth
                tmp12FloatArray[11] = kEntity.getClosePrice()

                mapPointsValue2Real(tmp12FloatArray)

                canvas.drawLines(tmp12FloatArray, barKChartPaint)
            }
            left += barWidth + spaceWidth
        }
    }

    private fun drawHollowKChart(canvas: Canvas) {
        hollowKChartPaint.strokeWidth = chartConfig.hollowChartLineStrokeWidth
        val barWidth = 1 * (1 - chartConfig.barSpaceRatio)
        val spaceWidth = 1 * chartConfig.barSpaceRatio
        var left = spaceWidth / 2f
        getKEntities().forEach { kEntity ->
            if (kEntity !is EmptyKEntity) {
                hollowKChartPaint.color =
                    if (kEntity.getClosePrice() >= kEntity.getOpenPrice()) stockChart.getConfig().riseColor else stockChart.getConfig().downColor

                if (kEntity.getClosePrice() >= kEntity.getOpenPrice()) { // 空心阳线
                    tmp24FloatArray[0] = left
                    tmp24FloatArray[1] = kEntity.getOpenPrice()
                    tmp24FloatArray[2] = left
                    tmp24FloatArray[3] = kEntity.getClosePrice()

                    tmp24FloatArray[4] = left
                    tmp24FloatArray[5] = kEntity.getOpenPrice()
                    tmp24FloatArray[6] = left + barWidth
                    tmp24FloatArray[7] = kEntity.getOpenPrice()

                    tmp24FloatArray[8] = left + barWidth
                    tmp24FloatArray[9] = kEntity.getOpenPrice()
                    tmp24FloatArray[10] = left + barWidth
                    tmp24FloatArray[11] = kEntity.getClosePrice()

                    tmp24FloatArray[12] = left
                    tmp24FloatArray[13] = kEntity.getClosePrice()
                    tmp24FloatArray[14] = left + barWidth
                    tmp24FloatArray[15] = kEntity.getClosePrice()

                    tmp24FloatArray[16] = left + barWidth / 2
                    tmp24FloatArray[17] = kEntity.getHighPrice()
                    tmp24FloatArray[18] = left + barWidth / 2
                    tmp24FloatArray[19] = max(kEntity.getOpenPrice(), kEntity.getClosePrice())

                    tmp24FloatArray[20] = left + barWidth / 2
                    tmp24FloatArray[21] = kEntity.getLowPrice()
                    tmp24FloatArray[22] = left + barWidth / 2
                    tmp24FloatArray[23] = min(kEntity.getOpenPrice(), kEntity.getClosePrice())

                    mapPointsValue2Real(tmp24FloatArray)
                    canvas.drawLines(tmp24FloatArray, hollowKChartPaint)
                } else { // 实心阴线
                    tmp4FloatArray[0] = left + barWidth / 2
                    tmp4FloatArray[1] = kEntity.getHighPrice()
                    tmp4FloatArray[2] = left + barWidth / 2
                    tmp4FloatArray[3] = kEntity.getLowPrice()
                    mapPointsValue2Real(tmp4FloatArray)
                    canvas.drawLines(tmp4FloatArray, hollowKChartPaint)
                    tmpRectF.left = left
                    tmpRectF.top = kEntity.getOpenPrice()
                    tmpRectF.right = left + barWidth
                    tmpRectF.bottom = kEntity.getClosePrice()
                    mapRectValue2Real(tmpRectF)
                    canvas.drawRect(tmpRectF, hollowKChartPaint)
                }

            }
            left += barWidth + spaceWidth
        }
    }

    private fun drawCandleKChart(canvas: Canvas) {
        candleKChartPaint.strokeWidth = chartConfig.candleChartLineStrokeWidth
        val barWidth = 1 * (1 - chartConfig.barSpaceRatio)
        val spaceWidth = 1 * chartConfig.barSpaceRatio
        var left = spaceWidth / 2f
        getKEntities().forEach { kEntity ->
            if (kEntity !is EmptyKEntity) {
                candleKChartPaint.color =
                    if (kEntity.getClosePrice() >= kEntity.getOpenPrice()) stockChart.getConfig().riseColor else stockChart.getConfig().downColor
                candleKChartPaint.color = candleKChartPaint.color
                tmp4FloatArray[0] = left + barWidth / 2
                tmp4FloatArray[1] = kEntity.getHighPrice()
                tmp4FloatArray[2] = left + barWidth / 2
                tmp4FloatArray[3] = kEntity.getLowPrice()
                mapPointsValue2Real(tmp4FloatArray)
                canvas.drawLines(tmp4FloatArray, candleKChartPaint)
                tmpRectF.left = left
                tmpRectF.top = kEntity.getOpenPrice()
                tmpRectF.right = left + barWidth
                tmpRectF.bottom = kEntity.getClosePrice()
                mapRectValue2Real(tmpRectF)
                canvas.drawRect(tmpRectF, candleKChartPaint)
            }
            left += barWidth + spaceWidth
        }
    }

    private fun drawLineKChart(canvas: Canvas) {
        lineKChartLinePaint.strokeWidth = chartConfig.lineChartStrokeWidth
        lineKChartLinePaint.color = chartConfig.lineChartColor
        var preIdx = -1
        for (idx in getKEntities().indices) {
            if (getKEntities()[idx] is EmptyKEntity) {
                preIdx = -1
                continue
            }

            if (preIdx == -1 || getKEntities()[idx] is KEntityOfLineStarter) {
                preIdx = idx
                continue
            }

            tmp4FloatArray[0] = preIdx + 0.5f
            tmp4FloatArray[1] = getKEntities()[preIdx].getClosePrice()
            tmp4FloatArray[2] = idx + 0.5f
            tmp4FloatArray[3] = getKEntities()[idx].getClosePrice()
            mapPointsValue2Real(tmp4FloatArray)
            canvas.drawLine(
                tmp4FloatArray[0],
                tmp4FloatArray[1],
                tmp4FloatArray[2],
                tmp4FloatArray[3],
                lineKChartLinePaint
            )
            preIdx = idx
        }
        if (chartConfig.showAvgLine) {
            avgPriceLinePaint.strokeWidth = chartConfig.avgLineStrokeWidth
            avgPriceLinePaint.color = chartConfig.avgLineColor
            var preAvgIdx = -1
            for (idx in getKEntities().indices) {
                if (getKEntities()[idx] is EmptyKEntity || getKEntities()[idx].getAvgPrice() == null) {
                    preAvgIdx = -1
                    continue
                }

                if (preAvgIdx == -1 || getKEntities()[idx] is KEntityOfLineStarter) {
                    preAvgIdx = idx
                    continue
                }

                tmp4FloatArray[0] = preAvgIdx + 0.5f
                tmp4FloatArray[1] = getKEntities()[preAvgIdx].getAvgPrice()!!
                tmp4FloatArray[2] = idx + 0.5f
                tmp4FloatArray[3] = getKEntities()[idx].getAvgPrice()!!
                mapPointsValue2Real(tmp4FloatArray)
                canvas.drawLine(
                    tmp4FloatArray[0],
                    tmp4FloatArray[1],
                    tmp4FloatArray[2],
                    tmp4FloatArray[3],
                    avgPriceLinePaint
                )
                preAvgIdx = idx
            }
        }
    }

    private fun drawLabels(canvas: Canvas) {

        fun doDraw(isLeft: Boolean, config: KChartConfig.LabelConfig) {
            if (config.count > 0) {
                labelPaint.textSize = config.textSize
                labelPaint.color = config.textColor
                labelPaint.getFontMetrics(tmpFontMetrics)
                val labelHeight = tmpFontMetrics.bottom - tmpFontMetrics.top
                val areaTop =
                    getChartDisplayArea().top + drawnIndexTextHeight + config.marginTop
                val areaBottom = getChartDisplayArea().bottom - config.marginBottom
                var verticalSpace = 0f
                if (config.count > 1) {
                    verticalSpace =
                        (areaBottom - areaTop - config.count * labelHeight) / (config.count - 1)
                }
                var pos = areaTop
                for (i in 1..config.count) {
                    tmp2FloatArray[0] = 0f
                    tmp2FloatArray[1] = pos + labelHeight / 2
                    mapPointsReal2Value(tmp2FloatArray)
                    val text = config.formatter.invoke(tmp2FloatArray[1])
                    val startX = if (isLeft) {
                        config.horizontalMargin
                    } else {
                        getChartDisplayArea().right - config.horizontalMargin - labelPaint.measureText(
                            text
                        )
                    }
                    canvas.drawText(text, startX, pos - tmpFontMetrics.top, labelPaint)
                    pos += verticalSpace + labelHeight
                }
            }
        }

        chartConfig.leftLabelConfig?.let { config ->
            doDraw(true, config)
        }

        chartConfig.rightLabelConfig?.let { config ->
            doDraw(false, config)
        }
    }
}