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

import androidx.annotation.ColorInt
import androidx.annotation.UiThread
import com.github.wangyiqian.stockchart.childchart.base.*
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.listener.OnLoadMoreListener
import com.github.wangyiqian.stockchart.util.checkMainThread

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/7
 */
class StockChartConfig {

    // K线数据
    var kEntities = mutableListOf<IKEntity>()
        set(value) {
            setKEntities(value, 0, kEntities.size - 1)
        }

    internal var setKEntitiesFlag = false

    internal var modifyKEntitiesFlag = false

    internal var appendKEntitiesFlag = false

    // 初始显示区域的起始坐标
    var showStartIndex = 0

    // 初始显示区域的结束坐标
    var showEndIndex = 0

    // 是否支持滑动
    var scrollAble =
        DEFAULT_SCROLL_ABLE

    // 是否支持"滑过头回弹"效果
    var overScrollAble =
        DEFAULT_OVER_SCROLL_ABLE

    // "滑过头回弹"最大距离
    var overScrollDistance =
        DEFAULT_OVER_SCROLL_DISTANCE

    // "滑过头回弹"过程中触发加载更多需要的距离
    var overScrollOnLoadMoreDistance =
        DEFAULT_OVER_SCROLL_ON_LOAD_MORE_DISTANCE

    // 是否支持双指缩放
    var scaleAble =
        DEFAULT_SCALE_ABLE

    // 是否需要"平滑的"滑动。如果false，滑动时一个下标对应的内容要么全显示，要么不显示。
    var scrollSmoothly =
        DEFAULT_SCROLL_SMOOTHLY

    // 超出滑动限制范围时拖动的"摩擦力"
    var frictionScrollExceedLimit =
        DEFAULT_FRICTION_SCROLL_EXCEED_LIMIT

    // 双指缩放最大缩放比例
    var scaleFactorMax =
        DEFAULT_SCALE_FACTOR_MAX
        set(value) {
            check(value >= 1f) { "The factor of max must not be less than 1.0 " }
            field = value
        }

    // 双指缩放最小缩放比例
    var scaleFactorMin =
        DEFAULT_SCALE_FACTOR_MIN
        set(value) {
            check(value <= 1f) { "The factor of max must not be greater than 1.0 " }
            field = value
        }

    // 是否支持长按高亮横线
    var showHighlightHorizontalLine =
        DEFAULT_SHOW_HIGHLIGHT_HORIZONTAL_LINE

    // 长按高亮横线宽度
    var highlightHorizontalLineWidth =
        DEFAULT_HIGHLIGHT_HORIZONTAL_LINE_WIDTH

    // 长按高亮横线颜色
    var highlightHorizontalLineColor =
        DEFAULT_HIGHLIGHT_HORIZONTAL_LINE_COLOR

    // 是否支持长按高亮竖线
    var showHighlightVerticalLine =
        DEFAULT_SHOW_HIGHLIGHT_VERTICAL_LINE

    // 长按高亮竖线宽度
    var highlightVerticalLineWidth =
        DEFAULT_HIGHLIGHT_VERTICAL_LINE_WIDTH

    // 长按高亮竖线颜色
    var highlightVerticalLineColor =
        DEFAULT_HIGHLIGHT_VERTICAL_LINE_COLOR

    // 涨色值
    var riseColor: Int =
        DEFAULT_RISE_COLOR

    // 跌色值
    var downColor: Int =
        DEFAULT_DOWN_COLOR

    // 背景色
    @ColorInt
    var backgroundColor = DEFAULT_BACKGROUND_COUNT

    // 背景网格横线数
    var gridHorizontalLineCount = DEFAULT_GRID_HORIZONTAL_LINE_COUNT

    // 背景网格竖线数
    var gridVerticalLineCount = DEFAULT_GRID_VERTICAL_LINE_COUNT

    // 背景网格线条色
    @ColorInt
    var gridLineColor = DEFAULT_GRID_LINE_COLOR

    // 背景网格线条宽度
    var gridLineStrokeWidth = DEFAULT_GRID_LINE_STROKE_WIDTH

    val childChartFactories = mutableListOf<AbsChildChartFactory<*>>()

    private var onLoadMoreListeners = mutableSetOf<OnLoadMoreListener>()

    fun addChildCharts(vararg childChartFactories: AbsChildChartFactory<*>) {
        this.childChartFactories.addAll(childChartFactories.toList())
    }

    fun removeChildCharts(vararg childChartFactories: AbsChildChartFactory<*>) {
        childChartFactories.forEach {
            this.childChartFactories.remove(it)
        }
    }

    /**
     * 设置K线数据
     * @param kEntities         所有数据集合
     * @param showStartIndex    显示区域起点对应数据集合的下标
     * @param showEndIndex      显示区域终点对应数据集合的下标
     */
    fun setKEntities(
        kEntities: List<IKEntity>,
        showStartIndex: Int = 0,
        showEndIndex: Int = if (kEntities.isEmpty()) 0 else kEntities.size - 1
    ) {
        check(showStartIndex <= showEndIndex) { "The value of showStartIndex must be less than showEndIndex." }
        if (kEntities.isNotEmpty()) {
            check(showStartIndex in kEntities.indices && showEndIndex in kEntities.indices) { "The value of showStartIndex and showEndIndex must be in the range of kEntities indexes." }
        }
        this.kEntities.clear()
        this.kEntities.addAll(kEntities)
        this.showStartIndex = showStartIndex
        this.showEndIndex = showEndIndex
        setKEntitiesFlag = true
    }

    /**
     * 修改K线数据
     * @param index 对应下标
     * @param kEntity 新数据
     */
    fun modifyKEntity(index: Int, kEntity: IKEntity) {
        check(index in 0 until kEntities.size) { "Index $index out of bounds for length ${kEntities.size}" }
        kEntities[index] = kEntity
        modifyKEntitiesFlag = true
    }

    /**
     * 追加K线数据
     */
    fun appendRightKEntities(kEntities: List<IKEntity>) {
        if (this.kEntities.isEmpty()) {
            setKEntities(kEntities)
        } else {
            this.kEntities.addAll(kEntities)
            appendKEntitiesFlag = true
        }
    }

    /**
     * 追加K线数据
     */
    fun appendLeftKEntities(kEntities: List<IKEntity>) {
        if (this.kEntities.isEmpty()) {
            setKEntities(kEntities)
        } else {
            showStartIndex += kEntities.size
            showEndIndex += kEntities.size
            this.kEntities.addAll(0, kEntities)
            appendKEntitiesFlag = true

        }
    }

    /**
     * 获取所有K线数据总数
     */
    fun getKEntitiesSize() = kEntities.size

    /**
     * 添加"加载更多"监听
     */
    fun addOnLoadMoreListener(listener: OnLoadMoreListener) {
        onLoadMoreListeners.add(listener)
    }

    /**
     * 移除"加载更多"监听
     */
    fun removeOnLoadMoreListener(listener: OnLoadMoreListener) {
        onLoadMoreListeners.remove(listener)
    }

    fun getOnLoadMoreListeners() = onLoadMoreListeners

}