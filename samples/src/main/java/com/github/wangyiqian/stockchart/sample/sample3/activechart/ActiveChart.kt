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

package com.github.wangyiqian.stockchart.sample.sample3.activechart

import android.graphics.*
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChart
import com.github.wangyiqian.stockchart.entities.*
import com.github.wangyiqian.stockchart.sample.sample3.data.ActiveInfo
import com.github.wangyiqian.stockchart.sample.sample3.data.IActiveChartKEntity
import java.lang.Float.max
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/5/14
 */

class ActiveChart(stockChart: IStockChart, chartConfig: ActiveChartConfig) :
    BaseChildChart<ActiveChartConfig>(stockChart, chartConfig) {

    private val mountainKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    private val mountainGradientKChartPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isDither = true }
    private var mountainLinearGradient: LinearGradient? = null
    private var mountainLinearGradientColors = intArrayOf()
    private val preCloseLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val timeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val timeTextFormat: DateFormat = SimpleDateFormat("HH:mm")
    private val tmpDate = Date()
    private val activePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val activeIndustryTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val activeRectCache = mutableListOf<Triple<RectF, Int, ActiveInfo?>>()
    private var activeRectSize = 0

    private val activeCircleRectCache = mutableListOf<RectF>()
    private var activeCircleRectSize = 0

    private var lastTapKEntityIdxOfActiveInfo = -1
    private var lastTapActiveInfo: ActiveInfo? = null

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
        var yMin = 0f
        var yMax = 0f
        getKEntities().filterIndexed { index, kEntity -> index in startIndex..endIndex && !kEntity.containFlag(FLAG_EMPTY) }
            .apply {
                forEachIndexed { index, kEntity ->
                    if (index == 0) {
                        yMin = kEntity.getClosePrice()
                        yMax = kEntity.getClosePrice()
                    } else {
                        yMin = kotlin.math.min(yMin, kEntity.getClosePrice())
                        yMax = kotlin.math.max(yMax, kEntity.getClosePrice())
                    }
                }
            }

        chartConfig.preClosePrice?.let { preClosePrice ->
            val ratio1 = (yMin - preClosePrice) / preClosePrice
            val ratio2 = (yMax - preClosePrice) / preClosePrice
            val ratioAbs = max(abs(ratio1), abs(ratio2))
            yMin = preClosePrice - preClosePrice * ratioAbs
            yMax = preClosePrice + preClosePrice * ratioAbs
        }

        result[0] = yMin
        result[1] = yMax
    }

    override fun drawAddition(canvas: Canvas) {
        drawActiveInfoCircle(canvas)
        drawActiveInfoLineOrRect(canvas, true)
        drawActiveInfoLineOrRect(canvas, false)
        drawTimeText(canvas)
    }

    override fun drawBackground(canvas: Canvas) {
    }

    override fun drawData(canvas: Canvas) {
        drawMountainKChart(canvas)
        drawPreCloseLine(canvas)
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
            if (getKEntities()[idx].containFlag(FLAG_EMPTY) || getKEntities()[idx].containFlag(FLAG_LINE_STARTER)) {
                if (preIdx != -1) {
                    tmpPath.lineTo(preIdx + 0.5f, 0f)
                    mapPathValue2Real(tmpPath)
                    canvas.drawPath(tmpPath, mountainGradientKChartPaint)
                    tmpPath.reset()
                }
                preIdx = -1
                if (getKEntities()[idx].containFlag(FLAG_EMPTY)) {
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
            if (getKEntities()[idx].containFlag(FLAG_EMPTY)) {
                preIdx = -1
                continue
            }

            if (preIdx == -1 || getKEntities()[idx].containFlag(FLAG_LINE_STARTER)) {
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

    private fun drawPreCloseLine(canvas: Canvas) {
        chartConfig.preClosePrice?.let {
            preCloseLinePaint.color = chartConfig.preClosePriceLineColor
            preCloseLinePaint.strokeWidth = chartConfig.preCloseLineStrokeWidth
            preCloseLinePaint.pathEffect = chartConfig.preCloseLinePathEffect
            tmp2FloatArray[0] = 0f
            tmp2FloatArray[1] = it
            mapPointsValue2Real(tmp2FloatArray)
            canvas.drawLine(
                getChartMainDisplayArea().left,
                tmp2FloatArray[1],
                getChartMainDisplayArea().right,
                tmp2FloatArray[1],
                preCloseLinePaint
            )
        }
    }

    private fun drawTimeText(canvas: Canvas) {
        timeTextPaint.color = chartConfig.timeTextColor
        timeTextPaint.textSize = chartConfig.timeTextSize
        timeTextPaint.getFontMetrics(tmpFontMetrics)
        val textY =
            getChartDisplayArea().bottom - tmpFontMetrics.bottom - chartConfig.timeTextMarginV

        if (chartConfig.fixTimeLeft != null) {
            chartConfig.fixTimeLeft?.let { text ->
                val textX = getChartMainDisplayArea().left + chartConfig.timeTextMarginH
                canvas.drawText(text, textX, textY, timeTextPaint)
            }
        } else {
            stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()?.let { idx ->
                val kEntity = getKEntities()[idx]
                tmpDate.time = kEntity.getTime()
                val text = timeTextFormat.format(tmpDate)
                val textX = getChartMainDisplayArea().left + chartConfig.timeTextMarginH
                canvas.drawText(text, textX, textY, timeTextPaint)
            }
        }

        if (chartConfig.fixTimeRight != null) {
            chartConfig.fixTimeRight?.let { text ->
                val textWidth = timeTextPaint.measureText(text)
                val textX = getChartMainDisplayArea().right - chartConfig.timeTextMarginH - textWidth
                canvas.drawText(text, textX, textY, timeTextPaint)
            }
        } else {
            stockChart.findLastNotEmptyKEntityIdxInDisplayArea()?.let { idx ->
                val kEntity = getKEntities()[idx]
                tmpDate.time = kEntity.getTime()
                val text = timeTextFormat.format(tmpDate)
                val textWidth = timeTextPaint.measureText(text)
                val textX = getChartMainDisplayArea().right - chartConfig.timeTextMarginH - textWidth
                canvas.drawText(text, textX, textY, timeTextPaint)
            }
        }
    }

    /**
     * 绘制异动指示线或异动块，分开两次绘制是为了要把所有指示线压在方块下面
     *
     * @param isDrawLine true:只绘制异动指示线 false:只绘制异动块
     */
    private fun drawActiveInfoLineOrRect(canvas: Canvas, isDrawLine: Boolean) {
        val firstKEntityIdx = stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()
        val lastKEntityIdx = stockChart.findLastNotEmptyKEntityIdxInDisplayArea()
        activeRectSize = 0

        if (firstKEntityIdx != null && lastKEntityIdx != null) {
            for (i in firstKEntityIdx..lastKEntityIdx) {
                val kEntity = getKEntities()[i]
                if (kEntity is IActiveChartKEntity) {
                    kEntity.getActiveInfo()?.let {

                        tmp2FloatArray[0] = i.toFloat()
                        tmp2FloatArray[1] = kEntity.getClosePrice()
                        mapPointsValue2Real(tmp2FloatArray)
                        val cx = tmp2FloatArray[0]
                        val cy = tmp2FloatArray[1]

                        if (activeRectSize == activeRectCache.size) {
                            // 需要扩容
                            resizeActiveRectCache(activeRectSize)
                        }
                        val displayCenterY =
                            (getChartDisplayArea().bottom + getChartDisplayArea().top) / 2
                        val isAbove = cy > displayCenterY
                        activePaint.color = if (it.red) {
                            chartConfig.activeColorRed
                        } else {
                            chartConfig.activeColorGreen
                        }
                        activePaint.style = Paint.Style.FILL
                        activeIndustryTextPaint.textSize = chartConfig.activeIndustryTextSize
                        activeIndustryTextPaint.color = chartConfig.activeIndustryTextColor
                        activeIndustryTextPaint.getFontMetrics(tmpFontMetrics)
                        val textWidth = activeIndustryTextPaint.measureText(it.industry)
                        val rectWidth = textWidth + chartConfig.activeIndustryTextPaddingH * 2
                        val rectHeight =
                            tmpFontMetrics.bottom - tmpFontMetrics.top + chartConfig.activeIndustryTextPaddingV * 2
                        val activeRectIdx = activeRectSize++
                        ensureActiveRectPosition(i, activeRectIdx, rectWidth, rectHeight, isAbove)
                        activeRectCache[activeRectIdx]?.apply {
                            second = i
                            third = it
                        }
                        val activeRect = activeRectCache[activeRectIdx].first
                        if (!isDrawLine) {
                            // 绘制异动行业文字和背景
                            canvas.drawRect(activeRect, activePaint)
                            val textX = activeRect.left + chartConfig.activeIndustryTextPaddingH
                            val textY =
                                activeRect.bottom - chartConfig.activeIndustryTextPaddingV - tmpFontMetrics.bottom
                            canvas.drawText(it.industry, textX, textY, activeIndustryTextPaint)
                        }

                        if (isDrawLine) {
                            // 绘制指示线
                            activePaint.strokeWidth = chartConfig.activeLineStrokeWidth
                            var lineStartY = 0f
                            var lineStopY = 0f
                            if (activeRect.bottom < cy - chartConfig.activeCircleRadius) {
                                lineStartY = activeRect.top
                                lineStopY = cy - chartConfig.activeCircleRadius
                            } else {
                                lineStartY = activeRect.bottom
                                lineStopY = cy + chartConfig.activeCircleRadius
                            }
                            canvas.drawLine(cx, lineStartY, cx, lineStopY, activePaint)
                        }
                    }
                }
            }
        }
    }

    /**
     * 绘制异动圆圈（必须在移动线和移动块之前绘制）
     */
    private fun drawActiveInfoCircle(canvas: Canvas) {
        val firstKEntityIdx = stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()
        val lastKEntityIdx = stockChart.findLastNotEmptyKEntityIdxInDisplayArea()
        activeCircleRectSize = 0

        if (firstKEntityIdx != null && lastKEntityIdx != null) {

            for (i in firstKEntityIdx..lastKEntityIdx) {
                val kEntity = getKEntities()[i]
                if (kEntity is IActiveChartKEntity) {
                    kEntity.getActiveInfo()?.let {
                        if (activeCircleRectSize == activeCircleRectCache.size) {
                            // 需要扩容
                            resizeActiveCircleRectCache(activeCircleRectSize)
                        }
                        tmp2FloatArray[0] = i.toFloat()
                        tmp2FloatArray[1] = kEntity.getClosePrice()
                        mapPointsValue2Real(tmp2FloatArray)
                        val cx = tmp2FloatArray[0]
                        val cy = tmp2FloatArray[1]

                        val activePaintColor = if (it.red) {
                            chartConfig.activeColorRed
                        } else {
                            chartConfig.activeColorGreen
                        }

                        lastTapActiveInfo?.let { lastTapActiveInfo ->
                            if (lastTapKEntityIdxOfActiveInfo == i && lastTapActiveInfo == it) { // 被点击的
                                val radius = chartConfig.activeCircleRadius * 3
                                // 绘制被点击的光晕
                                val activeSelectedRadialGradient = RadialGradient(
                                    cx,
                                    cy,
                                    radius,
                                    intArrayOf(activePaintColor, Color.TRANSPARENT),
                                    floatArrayOf(0.5f, 1f),
                                    Shader.TileMode.CLAMP
                                )
                                activePaint.apply {
                                    color = activePaintColor
                                    style = Paint.Style.FILL
                                    shader = activeSelectedRadialGradient
                                }
                                canvas.drawCircle(
                                    cx,
                                    cy,
                                    radius,
                                    activePaint
                                )
                                activePaint.shader = null
                            }
                        }

                        // 绘制圆圈底部背景色
                        activePaint.apply {
                            color = stockChart.getConfig().backgroundColor
                            style = Paint.Style.FILL
                        }
                        canvas.drawCircle(cx, cy, chartConfig.activeCircleRadius, activePaint)


                        // 绘制圆圈
                        activePaint.apply {
                            color = activePaintColor
                            strokeWidth = chartConfig.activeCircleStrokeWidth
                            style = Paint.Style.STROKE
                        }
                        canvas.drawCircle(cx, cy, chartConfig.activeCircleRadius, activePaint)
                        val activeCircleRectIdx = activeCircleRectSize++
                        activeCircleRectCache[activeCircleRectIdx].apply {
                            left = cx - chartConfig.activeCircleRadius
                            right = cx + chartConfig.activeCircleRadius
                            top = cy - chartConfig.activeCircleRadius
                            bottom = cy + chartConfig.activeCircleRadius
                        }
                    }
                }
            }
        }
    }

    /**
     * activeRectCache扩容
     */
    private fun resizeActiveRectCache(usedSize: Int) {
        if (usedSize >= activeRectCache.size) {
            for (i in 0..100) {
                activeRectCache.add(Triple(RectF(), -1, null))
            }
        }
    }

    /**
     * activeCircleRectCache扩容
     */
    private fun resizeActiveCircleRectCache(usedSize: Int) {
        if (usedSize >= activeCircleRectCache.size) {
            for (i in 0..100) {
                activeCircleRectCache.add(RectF())
            }
        }
    }

    /**
     * 计算出当前要绘制的异动信息块位置
     *
     * @param kEntityIdx K线点下标
     * @param activeRectIdx 需要在[activeRectCache]记录计算结果的下标
     * @param rectWidth 信息块宽度
     * @param rectHeight 信息块高度
     * @param isAbove 优先绘制在异动点的上方
     */
    private fun ensureActiveRectPosition(
        kEntityIdx: Int,
        activeRectIdx: Int,
        rectWidth: Float,
        rectHeight: Float,
        isAbove: Boolean
    ) {
        if (activeRectIdx >= activeRectCache.size) return

        val span = getChartDisplayArea().height() / 100
        val kEntity = getKEntities()[kEntityIdx]
        tmp2FloatArray[0] = kEntityIdx.toFloat()
        tmp2FloatArray[1] = kEntity.getClosePrice()
        mapPointsValue2Real(tmp2FloatArray)
        val cx = tmp2FloatArray[0]
        val cy = tmp2FloatArray[1]

        tmpRectF.apply {
            left = cx - chartConfig.activeCircleRadius
            right = cx + chartConfig.activeCircleRadius
            top = cy - chartConfig.activeCircleRadius
            bottom = cy + chartConfig.activeCircleRadius
        }


        val activeRect = activeRectCache[activeRectIdx].first

        initActiveRect(activeRect, cx, cy, rectWidth, rectHeight, isAbove)

        // 向下遍历
        var isTraverseDown = isAbove
        // 遍历方向改变过
        var isTraverseDirChanged = false

        while (isActiveRectIntersectWithBefore(activeRectIdx)
            || isActiveRectIntersectWithAllCircleRect(activeRectIdx)
        ) {
            var isEnd = false
            if (isTraverseDown) {
                if (activeRect.bottom + span >= getChartDisplayArea().bottom) {
                    isEnd = true
                }
            } else {
                if (activeRect.top - span <= getChartDisplayArea().top) {
                    isEnd = true
                }
            }

            if (isEnd) {
                if (isTraverseDirChanged)  // 两个方向都遍历过
                    break
                else { // 遍历了一个方向，换个方向遍历
                    initActiveRect(activeRect, cx, cy, rectWidth, rectHeight, isAbove)
                    isTraverseDown = !isTraverseDown
                    isTraverseDirChanged = true
                    continue
                }
            }

            if (isTraverseDown) {
                activeRect.top += span
                activeRect.bottom += span
            } else {
                activeRect.top -= span
                activeRect.bottom -= span
            }

        }

    }

    /**
     * 初始设置activeRect
     */
    private fun initActiveRect(
        activeRect: RectF,
        cx: Float,
        cy: Float,
        rectWidth: Float,
        rectHeight: Float,
        isAbove: Boolean
    ) {
        activeRect.apply {
            left =
                if (cx + rectWidth > getChartMainDisplayArea().right) cx - rectWidth else cx
            right = left + rectWidth
            val initLineLength = getChartDisplayArea().height() * 2 / 5
            if (isAbove) {
                top = cy - initLineLength
                bottom = top + rectHeight
            } else {
                bottom = cy + initLineLength
                top = bottom - rectHeight
            }
        }
    }

    /**
     * 异动块是否和前面的任意一个碰撞
     */
    private fun isActiveRectIntersectWithBefore(activeRectIdx: Int): Boolean {
        var target = activeRectCache[activeRectIdx].first
        for (i in 0 until activeRectIdx) {
            if (isIntersect(activeRectCache[i].first, target)) {
                return true
            }
        }
        return false
    }

    /**
     * 异动块是否和任意一个异动小圆点碰撞
     */
    private fun isActiveRectIntersectWithAllCircleRect(activeRectIdx: Int): Boolean {
        var target = activeRectCache[activeRectIdx].first
        var ext = getChartDisplayArea().height() / 30
        for (i in 0 until activeCircleRectSize) {
            tmpRectF.apply {
                set(activeCircleRectCache[i])
                left -= ext
                top -= ext
                right += ext
                bottom += ext
            }
            if (isIntersect(tmpRectF, target)) {
                return true
            }
        }
        return false
    }

    private val tmpRectFForIntersect1 = RectF()
    private val tmpRectFForIntersect2 = RectF()

    private fun isIntersect(rect1: RectF, rect2: RectF): Boolean {
        tmpRectFForIntersect1.set(rect1)
        tmpRectFForIntersect2.set(rect2)
        return tmpRectFForIntersect1.intersect(tmpRectFForIntersect2)
    }

    override fun drawHighlight(canvas: Canvas) {
    }

    override fun onKEntitiesChanged() {
    }

    override fun preDrawBackground(canvas: Canvas) {
    }

    override fun preDrawData(canvas: Canvas) {
    }

    override fun preDrawHighlight(canvas: Canvas) {
    }

    override fun onTap(event: GestureEvent) {
        chartConfig.onActiveIndustryClickListener?.let { onActiveIndustryClickListener ->
            for (i in 0..activeRectSize) {
                if (activeRectCache[i].first.contains(event.x, event.y)) {
                    activeRectCache[i].third?.let { activeInfo ->
                        lastTapActiveInfo = activeInfo
                        lastTapKEntityIdxOfActiveInfo = activeRectCache[i].second
                        stockChart.notifyChanged() // 选中光晕需要刷新
                        onActiveIndustryClickListener.onActiveIndustryClick(activeInfo)
                        return
                    }
                }
            }
        }
        if (lastTapActiveInfo != null) {
            lastTapKEntityIdxOfActiveInfo = -1
            lastTapActiveInfo = null
            stockChart.notifyChanged()
        }
    }
}

data class Triple<A, B, C>(
    var first: A,
    var second: B,
    var third: C
)