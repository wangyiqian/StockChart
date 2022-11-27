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

import android.graphics.Matrix
import android.graphics.PathEffect
import androidx.annotation.ColorInt
import com.github.wangyiqian.stockchart.childchart.base.*
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.listener.OnGestureListener
import com.github.wangyiqian.stockchart.listener.OnLoadMoreListener

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/7
 */
class StockChartConfig {

    /**
     * K线数据
     */
    var kEntities = mutableListOf<IKEntity>()
        set(value) {
            setKEntities(value, 0, kEntities.size - 1)
        }

    /**
     * 初始显示区域的起始坐标
     */
    var showStartIndex = 0

    /**
     * 初始显示区域的结束坐标
     */
    var showEndIndex = 0

    /**
     * 是否支持滑动
     */
    var scrollAble =
        DEFAULT_SCROLL_ABLE

    /**
     * 是否支持"滑过头回弹"效果
     */
    var overScrollAble =
        DEFAULT_OVER_SCROLL_ABLE

    /**
     * "滑过头回弹"最大距离
     */
    var overScrollDistance =
        DEFAULT_OVER_SCROLL_DISTANCE

    /**
     * "滑过头回弹"过程中触发加载更多需要的距离
     */
    var overScrollOnLoadMoreDistance =
        DEFAULT_OVER_SCROLL_ON_LOAD_MORE_DISTANCE

    /**
     * 是否支持双指缩放
     */
    var scaleAble =
        DEFAULT_SCALE_ABLE

    /**
     * 是否需要"平滑的"滑动。如果false，滑动时一个下标对应的内容要么全显示，要么不显示。
     */
    var scrollSmoothly = DEFAULT_SCROLL_SMOOTHLY

    /**
     * 超出滑动限制范围时拖动的"摩擦力"
     */
    var frictionScrollExceedLimit =
        DEFAULT_FRICTION_SCROLL_EXCEED_LIMIT

    /**
     * 双指缩放最大缩放比例
     */
    var scaleFactorMax =
        DEFAULT_SCALE_FACTOR_MAX
        set(value) {
            check(value >= 1f) { "The factor of max must not be less than 1.0 " }
            field = value
        }

    /**
     * 双指缩放最小缩放比例
     */
    var scaleFactorMin =
        DEFAULT_SCALE_FACTOR_MIN
        set(value) {
            check(value <= 1f) { "The factor of max must not be greater than 1.0 " }
            field = value
        }

    /**
     * 是否支持长按高亮横线
     */
    var showHighlightHorizontalLine =
        DEFAULT_SHOW_HIGHLIGHT_HORIZONTAL_LINE

    /**
     * 长按高亮横线宽度
     */
    var highlightHorizontalLineWidth =
        DEFAULT_HIGHLIGHT_HORIZONTAL_LINE_WIDTH

    /**
     * 长按高亮横线颜色
     */
    var highlightHorizontalLineColor =
        DEFAULT_HIGHLIGHT_HORIZONTAL_LINE_COLOR

    /**
     * 长按高亮线横线虚线配置
     */
    var highlightHorizontalLinePathEffect: PathEffect? = null

    /**
     * 是否支持长按高亮竖线
     */
    var showHighlightVerticalLine =
        DEFAULT_SHOW_HIGHLIGHT_VERTICAL_LINE

    /**
     * 长按高亮竖线宽度
     */
    var highlightVerticalLineWidth =
        DEFAULT_HIGHLIGHT_VERTICAL_LINE_WIDTH

    /**
     * 长按高亮竖线颜色
     */
    var highlightVerticalLineColor =
        DEFAULT_HIGHLIGHT_VERTICAL_LINE_COLOR

    /**
     * 长按高亮线竖线虚线配置
     */
    var highlightVerticalLinePathEffect: PathEffect? = null

    /**
     * 涨色值
     */
    var riseColor: Int =
        DEFAULT_RISE_COLOR

    /**
     * 跌色值
     */
    var downColor: Int =
        DEFAULT_DOWN_COLOR

    /**
     * 背景色
     */
    @ColorInt
    var backgroundColor = DEFAULT_BACKGROUND_COUNT

    /**
     * 背景网格横线数
     */
    var gridHorizontalLineCount = DEFAULT_GRID_HORIZONTAL_LINE_COUNT

    /**
     * 背景网格竖线数
     */
    var gridVerticalLineCount = DEFAULT_GRID_VERTICAL_LINE_COUNT

    /**
     * 背景网格线条色
     */
    @ColorInt
    var gridLineColor = DEFAULT_GRID_LINE_COLOR

    /**
     * 背景网格线条宽度
     */
    var gridLineStrokeWidth = DEFAULT_GRID_LINE_STROKE_WIDTH

    /**
     * 背景网格线条虚线配置
     */
    var gridLinePathEffect: PathEffect? = null

    /**
     * 背景网格横线第一条线的顶部偏移量
     */
    var horizontalGridLineTopOffsetCalculator: ((StockChart) -> Float)? = null

    /**
     * 背景网格横线左侧偏移量
     */
    var horizontalGridLineLeftOffsetCalculator: ((StockChart) -> Float)? = null

    /**
     * 背景网格横线间距
     */
    var horizontalGridLineSpaceCalculator: ((StockChart) -> Float)? = null

    var valueTendToZero = DEFAULT_VALUE_TEND_TO_ZERO

    /**
     * 主数据显示区域的左内间距，主数据一般指数据线
     */
    var chartMainDisplayAreaPaddingLeft: Float = 0f

    /**
     * 主数据显示区域的右内间距，主数据一般指数据线
     */
    var chartMainDisplayAreaPaddingRight: Float = 0f

    /**
     * 加载更多监听
     */
    var onLoadMoreListener: OnLoadMoreListener? = null
        set(value) {
            field?.apply { removeOnLoadMoreListener(this) }
            value?.apply { addOnLoadMoreListener(this) }
            field = value

        }

    /**
     * 手势监听
     */
    var onGestureListener: OnGestureListener? = null
        set(value) {
            field?.apply { removeOnGestureListener(this) }
            value?.apply { addOnGestureListener(this) }
            field = value
        }

    /**
     * 高级用法，每个子图最终绘制追加一个Matrix
     */
    var extMatrix: Matrix? = null

    val childChartFactories = mutableListOf<AbsChildChartFactory<*>>()

    private var onLoadMoreListeners = mutableSetOf<OnLoadMoreListener>()

    private var onGestureListeners = mutableSetOf<OnGestureListener>()

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
        check(index in this.kEntities.indices) { "Index $index out of bounds for length ${kEntities.size}" }
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
            modifyKEntitiesFlag = true
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
            modifyKEntitiesFlag = true

        }
    }

    /**
     * 插入K线数据
     */
    fun insertKEntities(index: Int, kEntities: List<IKEntity>) {
        check(index in this.kEntities.indices) { "Index $index out of bounds for length ${kEntities.size}" }
        if (index <= showEndIndex) {
            showEndIndex += kEntities.size
            showStartIndex += kEntities.size
        }
        this.kEntities.addAll(index, kEntities)
        modifyKEntitiesFlag = true
    }

    /**
     * 删除一个K线数据点
     */
    fun removeKEntity(index: Int) {
        check(index in this.kEntities.indices) { "Index $index out of bounds for length ${kEntities.size}" }
        if (index <= showEndIndex) {
            showEndIndex--
            showStartIndex--
        }
        this.kEntities.removeAt(index)
        modifyKEntitiesFlag = true
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

    /**
     * 添加手势监听
     */
    fun addOnGestureListener(listener: OnGestureListener) {
        onGestureListeners.add(listener)
    }

    /**
     * 移除手势监听
     */
    fun removeOnGestureListener(listener: OnGestureListener) {
        onGestureListeners.remove(listener)
    }

    internal fun getOnGestureListeners() = onGestureListeners
    internal var setKEntitiesFlag = false
    internal var modifyKEntitiesFlag = false

}