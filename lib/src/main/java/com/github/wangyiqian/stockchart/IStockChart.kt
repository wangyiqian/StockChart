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
     * 获取显示区域第一个不为空的K线数据点下标
     */
    fun findFirstNotEmptyKEntityIdxInDisplayArea(): Int?

    /**
     * 获取缩放比例
     */
    fun getTotalScaleX(): Float

}