package com.github.wangyiqian.stockchart.childchart.timebar

import android.graphics.Canvas
import android.graphics.Paint
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChart
import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.util.DimensionUtil
import java.util.*

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/22
 */
class TimeBar(stockChart: IStockChart, chartConfig: TimeBarConfig) :
    BaseChildChart<TimeBarConfig>(stockChart, chartConfig) {

    private val tmpDate = Date()

    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val highlightLabelBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)


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

        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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

        fun drawLabel(dayBeginKEntityIdx: Int, dayEndKEntityIdx: Int) {
            val time = getKEntities()[dayBeginKEntityIdx].getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)

            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            tmp2FloatArray[0] = (dayBeginKEntityIdx + dayEndKEntityIdx) / 2 + 0.5f
            tmp2FloatArray[1] = 0f
            mapPointsValue2Real(tmp2FloatArray)
            val centerRealX = tmp2FloatArray[0]

            if (centerRealX - labelHalfWidth < getChartDisplayArea().left || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
                return
            }

            val x = centerRealX - labelHalfWidth
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom
            canvas.drawText(label, x, y, labelPaint)
        }

        var dayBeginKEntityIdx: Int? = null
        var dayEndKEntityIdx: Int? = null
        var tmpLabel = ""

        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity is EmptyKEntity) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)

            if (tmpLabel != label) {
                if (dayBeginKEntityIdx != null && dayEndKEntityIdx != null) {
                    drawLabel(dayBeginKEntityIdx!!, dayEndKEntityIdx!!)
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
            drawLabel(dayBeginKEntityIdx!!, dayEndKEntityIdx!!)
        }
    }

    private fun drawLabelOfWeekType(canvas: Canvas) {

        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 70f)

        getKEntities().forEachIndexed { idx, kEntity ->
            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 100f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 30f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 40f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

            val time = kEntity.getTime()
            tmpDate.time = time
            val label = chartConfig.type.labelDateFormat.format(tmpDate)
            if (label == lastDrawLabel) {
                return@forEachIndexed
            }


            val labelWidth = labelPaint.measureText(label)
            val labelHalfWidth = labelWidth / 2

            var x = getChartDisplayArea().left
            val y =
                getChartDisplayArea().top + getChartDisplayArea().height() / 2 + (tmpFontMetrics.bottom - tmpFontMetrics.top) / 2 - tmpFontMetrics.bottom

            if (lastDrawLabel != "") {
                tmp2FloatArray[0] = idx + 0.5f
                tmp2FloatArray[1] = 0f
                mapPointsValue2Real(tmp2FloatArray)
                val centerRealX = tmp2FloatArray[0]
                if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 30f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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
        var lastDrawRight = getChartDisplayArea().left
        var lastDrawLabel = ""
        val labelMinSpace = DimensionUtil.dp2px(context, 50f)

        getKEntities().forEachIndexed { idx, kEntity ->

            if (kEntity is EmptyKEntity) return@forEachIndexed

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
            if (centerRealX - labelHalfWidth < lastDrawRight + labelMinSpace || centerRealX + labelHalfWidth > getChartDisplayArea().right) {
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

    private fun drawHighlightLabel(canvas: Canvas) {
        getHighlight()?.let { highlight ->

            if (!stockChart.getConfig().showHighlightVerticalLine) return

            val idx = highlight.getIdx()

            if (idx in getKEntities().indices) {

                val kEntity = getKEntities()[idx]

                if (kEntity is EmptyKEntity) return

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
                if (x - bgPadding < getChartDisplayArea().left) {
                    x = getChartDisplayArea().left + bgPadding
                }
                if (x + labelWidth + bgPadding > getChartDisplayArea().right) {
                    x = getChartDisplayArea().right - labelWidth - bgPadding
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