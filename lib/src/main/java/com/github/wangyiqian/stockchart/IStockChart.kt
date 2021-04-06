package com.github.wangyiqian.stockchart

import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import com.github.wangyiqian.stockchart.childchart.base.IChildChart
import com.github.wangyiqian.stockchart.entities.Highlight
import com.github.wangyiqian.stockchart.listener.OnKEntitiesChangedListener

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
interface IStockChart {

    fun setConfig(config: StockChartConfig)

    fun getConfig(): StockChartConfig

    /**
     * 获取全部子图
     */
    fun getChildCharts(): List<IChildChart>

    /**
     * 获取上下文
     */
    fun getContext(): Context

    /**
     * 获取整个StockChart的触摸范围
     */
    fun getTouchArea(): Rect

    /**
     * 刷新
     */
    fun notifyChanged()

    fun dispatchOnLeftLoadMore()

    fun dispatchOnRightLoadMore()

    fun addOnKEntitiesChangedListener(listener: OnKEntitiesChangedListener)

    fun removeOnKEntitiesChangedListener(listener: OnKEntitiesChangedListener)

    fun getXScaleMatrix(): Matrix

    fun getFixXScaleMatrix(): Matrix

    fun getScrollMatrix(): Matrix

    /**
     * 获取长按的信息
     */
    fun getHighlight(childChart: IChildChart): Highlight?


    /**
     * 获取显示区域最后一个不为空的K线数据点下标
     */
    fun findLastNotEmptyKEntityIdxInDisplayArea(): Int?

    /**
     * 获取显示区域最后一个不为空的K线数据点下标
     */
    fun findFirstNotEmptyKEntityIdxInDisplayArea(): Int?
}