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

package com.github.wangyiqian.stockchart.childchart.timebar

import android.graphics.Canvas
import android.graphics.Paint
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChart
import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.containFlag
import com.github.wangyiqian.stockchart.util.DimensionUtil
import java.util.*

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/22
 */
class TimeBar(stockChart: IStockChart, chartConfig: TimeBarConfig) :
    BaseChildChart<TimeBarConfig>(stockChart, chartConfig) {

    private val tmpDate by lazy { Date() }

    private val labelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val highlightLabelBgPaint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }

    override fun onKEntitiesChanged() {
    }

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
    }

    override fun preDrawBackground(canvas: Canvas) {
    }

    override fun drawBackground(canvas: Canvas) {
        canvas.drawColor(chartConfig.backGroundColor)
    }

    override fun preDrawData(canvas: Canvas) {
    }

    override fun drawData(canvas: Canvas) {

        labelPaint.textSize = chartConfig.labelTextSize
        labelPaint.color = chartConfig.labelTextColor
        labelPaint.getFontMetrics(tmpFontMetrics)

        when (chartConfig.type) {
            is TimeBarConfig.Type.Day -> drawLabelOfDayType(canvas)
            is TimeBarConfig.Type.FiveDays -> drawLabelOfFiveDaysType(canvas)
            is TimeBarConfig.Type.Week -> drawLabelOfWeekType(canvas)
            is TimeBarConfig.Type.Month -> drawLabelOfMonthType(canvas)
            is TimeBarConfig.Type.Quarter -> drawLabelOfQuarterType(canvas)
            is TimeBarConfig.Type.Year -> drawLabelOfYearType(canvas)
            is TimeBarConfig.Type.FiveYears -> drawLabelOfFiveYearsType(canvas)
            is TimeBarConfig.Type.YTD -> drawLabelOfYTDType(canvas)
            is TimeBarConfig.Type.OneMinute -> drawLabelOfOneMinuteType(canvas)
            is TimeBarConfig.Type.FiveMinutes -> drawLabelOfFiveMinutesType(canvas)
            is TimeBarConfig.Type.SixtyMinutes -> drawLabelOfSixtyMinutesType(canvas)
            is TimeBarConfig.Type.DayTime -> drawLabelOfDayTimeType(canvas)
        }
    }

    override fun preDrawHighlight(canvas: Canvas) {
    }

    override fun drawHighlight(canvas: Canvas) {

        highlightLabelPaint.textSize = chartConfig.highlightLabelTextSize
        highlightLabelPaint.color = chartConfig.highlightLabelTextColor
        highlightLabelPaint.getFontMetrics(tmpFontMetrics)
        highlightLabelBgPaint.color = chartConfig.highlightLabelBgColor
        drawHighlightLabel(canvas)
    }

    override fun drawAddition(canvas: Canvas) {
    }

    private fun drawLabelOfDayType(canvas: Canvas) {

        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfFiveDaysType(canvas: Canvas) {

        var dayBeginKEntityIdx: Int? = null
        var dayEndKEntityIdx: Int? = null
        var tmpLabel = ""

        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)

            if (tmpLabel != label) {
                if (dayBeginKEntityIdx != null && dayEndKEntityIdx != null) {
                    doDrawLabelOfFiveDaysType(canvas, dayBeginKEntityIdx!!, dayEndKEntityIdx!!)
                    dayBeginKEntityIdx = null
                    dayEndKEntityIdx = null
                }
            }

            tmpLabel = label

            if (dayBeginKEntityIdx == null) {
                dayBeginKEntityIdx = idx
            }

            dayEndKEntityIdx = idx
        }

        if (dayBeginKEntityIdx != null && dayEndKEntityIdx != null) {
            doDrawLabelOfFiveDaysType(canvas, dayBeginKEntityIdx!!, dayEndKEntityIdx!!)
        }
    }

    private fun doDrawLabelOfFiveDaysType(
        canvas: Canvas,
        dayBeginKEntityIdx: Int,
        dayEndKEntityIdx: Int
    ) {
        val time = getKEntities()[dayBeginKEntityIdx].getTime()
        tmpDate.time = time
        val label = chartConfig.type.labelDateFormat.format(tmpDate)

        val labelWidth = labelPaint.measureText(label)
        val labelHalfWidth = labelWidth / 2

        tmp2FloatArray[0] = (dayBeginKEntityIdx + dayEndKEntityIdx) / 2 + 0.5f
        tmp2FloatArray[1] = 0f
        mapPointsValue2Real(tmp2FloatArray)
        val centerRealX = tmp2FloatArray[0]

        if (centerRealX - labelHalfWidth < getChartMainDisplayArea().left || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
            return
        }

        val x = centerRealX - labelHalfWidth
        val y =
            getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
        canvas.drawText(label, x, y, labelPaint)
    }

    private fun drawLabelOfWeekType(canvas: Canvas) {

        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 70f)

        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }

    }

    private fun drawLabelOfMonthType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 100f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfQuarterType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfYearType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 30f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfFiveYearsType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfAllType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 40f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }


            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            var x = getChartMainDisplayArea().left
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

            if (lastDrawLabel != "") {
                tmp2FloatArray[0] = idx + 0.5f
                tmp2FloatArray[1] = 0f
                mapPointsValue2Real(tmp2FloatArray)
                val centerRealX = tmp2FloatArray[0]
                if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                    return@forEachIndexed
                }

                x = centerRealX - labelHalfWidth
            }

            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfYTDType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 30f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            lastDrawLabel = label

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfOneMinuteType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfFiveMinutesType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfSixtyMinutesType(canvas: Canvas) {
        var lastDrawRight = getChartMainDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity.containFlag(FLAG_EMPTY)) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = idx + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartMainDisplayArea().right) {
                return@forEachIndexed
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)

            lastDrawLabel = label
            lastDrawRight = x + labelWidth
        }
    }

    private fun drawLabelOfDayTimeType(canvas: Canvas) {

        stockChart.findFirstNotEmptyKEntityIdxInDisplayArea()?.let { idx ->
            doDrawLabelOfDayTimeType(canvas, idx)
        }

        stockChart.findLastNotEmptyKEntityIdxInDisplayArea()?.let { idx ->
            doDrawLabelOfDayTimeType(canvas, idx)
        }
    }

    private fun doDrawLabelOfDayTimeType(canvas: Canvas, idx: Int) {
        val labelMinSpace = DimensionUtil.dp2px(context, 5f)

        val kEntity = getKEntities()[idx]
        val time = kEntity.getTime()
        tmpDate.time = time
        val label = chartConfig.type.labelDateFormat.format(tmpDate)

        val labelWidth = labelPaint.measureText(label)
        val labelHalfWidth = labelWidth / 2

        tmp2FloatArray[0] = idx + 0.5f
        tmp2FloatArray[1] = 0f
        mapPointsValue2Real(tmp2FloatArray)
        val centerRealX = tmp2FloatArray[0]

        var x = centerRealX - labelHalfWidth
        if (x + labelWidth > getChartMainDisplayArea().right - labelMinSpace) x =
            getChartMainDisplayArea().right - labelMinSpace - labelWidth
        if (x < getChartMainDisplayArea().left + labelMinSpace) x =
            getChartMainDisplayArea().left + labelMinSpace
        val y =
            getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
        canvas.drawText(label, x, y, labelPaint)
    }

    private fun drawHighlightLabel(canvas: Canvas) {
        getHighlight()?.let { highlight ->

            if (!stockChart.getConfig().showHighlightVerticalLine) return

            val idx = highlight.getIdx()

            if (idx in getKEntities().indices) {

                val kEntity = getKEntities()[idx]

                if (kEntity.containFlag(FLAG_EMPTY)) return

                val time = kEntity.getTime()
                tmpDate.time = time
                val label = chartConfig.type.highlightLabelDateFormat.format(tmpDate)

                val labelWidth = highlightLabelPaint.measureText(label)
                val labelHalfWidth = labelWidth / 2

                tmp2FloatArray[0] = idx + 0.5f
                tmp2FloatArray[1] = 0f
                mapPointsValue2Real(tmp2FloatArray)
                val centerRealX = tmp2FloatArray[0]

                val bgPadding = 10f
                var x = centerRealX - labelHalfWidth
                if (x - bgPadding < getChartMainDisplayArea().left) {
                    x = getChartMainDisplayArea().left + bgPadding
                }
                if (x + labelWidth + bgPadding > getChartMainDisplayArea().right) {
                    x = getChartMainDisplayArea().right - labelWidth - bgPadding
                }
                val y =
                    getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
                canvas.drawRect(
                    x - bgPadding,
                    getChartDisplayArea().top,
                    x + labelWidth + bgPadding,
                    getChartDisplayArea().bottom,
                    highlightLabelBgPaint
                )
                canvas.drawText(label, x, y, highlightLabelPaint)
            }
        }

    }
}