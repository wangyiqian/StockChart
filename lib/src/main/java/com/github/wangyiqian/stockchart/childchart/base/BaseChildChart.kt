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

package com.github.wangyiqian.stockchart.childchart.base

import android.graphics.*
import android.view.View
import android.view.ViewGroup
import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.StockChart
import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.GestureEvent
import com.github.wangyiqian.stockchart.entities.containFlag
import com.github.wangyiqian.stockchart.listener.OnKEntitiesChangedListener

/**
 * 所有的StockChart的子View都需要继承此类
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
abstract class BaseChildChart<C : BaseChildChartConfig> @JvmOverloads constructor(
    val stockChart: IStockChart,
    val chartConfig: C
) : View(stockChart.getContext()), IChildChart,
    OnKEntitiesChangedListener {

    // 管理matrix
    private var childChartMatrixHelper =
        ChildChartMatrixHelper(
            stockChart,
            this
        )

    // 显示区域
    private val chartDisplayArea = RectF()

    // 主显示区域
    private val chartMainDisplayArea = RectF()

    // 临时载体
    protected val tmp2FloatArray by lazy { FloatArray(2) }
    protected val tmp4FloatArray by lazy { FloatArray(4) }
    protected val tmp12FloatArray by lazy { FloatArray(12) }
    protected val tmp24FloatArray by lazy { FloatArray(24) }
    protected val tmpRectF by lazy { RectF() }
    protected val tmpRect by lazy { Rect() }
    protected val tmpPath by lazy { Path() }
    protected val tmpFontMetrics by lazy { Paint.FontMetrics() }

    init {
        val layoutParams =
            StockChart.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                chartConfig.height
            )
        layoutParams.setMargins(
            0,
            chartConfig.marginTop,
            0,
            chartConfig.marginBottom
        )
        this.layoutParams = layoutParams

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        stockChart.addOnKEntitiesChangedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stockChart.removeOnKEntitiesChangedListener(this)
    }

    override fun getHighlight() = stockChart.getHighlight(this)

    override fun getKEntities() = stockChart.getConfig().kEntities

    override fun view() = this

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setDisplayArea()
        prepare()
    }

    private fun setDisplayArea() {

        // x轴显示区域固定死 子类不可各自实现
        chartDisplayArea.apply {
            left = 0f
            right = width.toFloat()
            top = getDisplayAreaYRangeMin()
            bottom = getDisplayAreaYRangeMax()
        }

        chartMainDisplayArea.apply {
            left = chartDisplayArea.left + stockChart.getConfig().chartMainDisplayAreaPaddingLeft
            right = chartDisplayArea.right - stockChart.getConfig().chartMainDisplayAreaPaddingRight
            top = chartDisplayArea.top + chartConfig.chartMainDisplayAreaPaddingTop
            bottom = chartDisplayArea.bottom - chartConfig.chartMainDisplayAreaPaddingBottom
        }

    }

    /**
     * y轴显示区域最小值
     * 子类若有特殊需求可覆盖实现
     */
    open fun getDisplayAreaYRangeMin() = 0f

    /**
     * y轴显示区域最大值
     * 子类若有特殊需求可覆盖实现
     */
    open fun getDisplayAreaYRangeMax() = height.toFloat()

    override fun onSetKEntities() {
        onKEntitiesChanged()
        prepare()
    }

    override fun onModifyKEntities() {
        onKEntitiesChanged()
        prepare()
    }

    private fun prepare() {
        if (stockChart.getConfig().getKEntitiesSize() <= 0) return
        childChartMatrixHelper?.prepare()
    }

    /**
     * 模板方法：k线数据发送变化
     */
    abstract fun onKEntitiesChanged()

    /**
     * 获得指定下标范围内[startIndex ~ endIndex]，x轴逻辑坐标的范围值
     * 注意，应包含最后一个数据的长度
     * 统一规则，子类不可定制，
     */
    fun getXValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
        result[0] = startIndex.toFloat()
        result[1] = endIndex.toFloat() + getXValueUnitLen()
    }

    /**
     * x轴逻辑坐标单位长度，统一固定规则
     */
    private fun getXValueUnitLen() = 1f

    /**
     * 模板方法：获得指定下标范围内[startIndex ~ endIndex]，y轴逻辑坐标的范围值
     */
    abstract fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray)

    override fun onDraw(canvas: Canvas) {
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        if (stockChart.getConfig().getKEntitiesSize() <= 0) return
        childChartMatrixHelper?.setOnDraw()
        preDrawBackground(canvas)
        drawBackground(canvas)
        preDrawData(canvas)
        drawData(canvas)
        preDrawHighlight(canvas)
        drawHighlight(canvas)
        drawAddition(canvas)
    }

    /**
     * 模板方法：在绘制背景之前绘制
     */
    abstract fun preDrawBackground(canvas: Canvas)

    /**
     * 模板方法：绘制背景
     */
    abstract fun drawBackground(canvas: Canvas)

    /**
     * 模板方法：在绘制主数据之前绘制
     */
    abstract fun preDrawData(canvas: Canvas)

    /**
     * 模板方法：绘制主数据
     */
    abstract fun drawData(canvas: Canvas)

    /**
     * 模板方法：在绘制长按高亮之前绘制
     */
    abstract fun preDrawHighlight(canvas: Canvas)

    /**
     * 模板方法：绘制长按高亮
     */
    abstract fun drawHighlight(canvas: Canvas)

    /**
     * 模板方法：绘制其他内容
     */
    abstract fun drawAddition(canvas: Canvas)

    override fun getCoordinateMatrix() = childChartMatrixHelper!!.coordinateMatrix

    override fun mapPointsValue2Real(pts: FloatArray) {
        childChartMatrixHelper?.mapPointsValue2Real(pts)
    }

    override fun mapRectValue2Real(rect: RectF) {
        childChartMatrixHelper?.mapRectValue2Real(rect)
    }

    override fun mapPathValue2Real(path: Path) {
        childChartMatrixHelper?.mapPathValue2Real(path)
    }

    override fun mapPointsReal2Value(pts: FloatArray) {
        childChartMatrixHelper?.mapPointsReal2Value(pts)
    }

    override fun mapRectReal2Value(rect: RectF) {
        childChartMatrixHelper?.mapRectReal2Value(rect)
    }

    override fun mapPathReal2Value(path: Path) {
        childChartMatrixHelper?.mapPathReal2Value(path)
    }

    override fun getChartDisplayArea() = chartDisplayArea

    override fun getChartMainDisplayArea() = chartMainDisplayArea

    override fun getConfig() = chartConfig

    override fun onTap(event: GestureEvent) {}

    override fun isRise(idx: Int) =
        if (getKEntities()[idx].getClosePrice() == getKEntities()[idx].getOpenPrice()) {
            if (idx - 1 in getKEntities().indices) {
                val preKEntity = getKEntities()[idx - 1]
                if (!preKEntity.containFlag(FLAG_EMPTY)) {
                    getKEntities()[idx].getClosePrice() >= preKEntity.getClosePrice()
                } else {
                    true
                }
            } else {
                true
            }
        } else {
            getKEntities()[idx].getClosePrice() > getKEntities()[idx].getOpenPrice()
        }
}