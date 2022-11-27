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
import android.widget.OverScroller
import kotlin.math.abs

/**
 * 管理StockChart的Matrix
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/6
 */
internal class MatrixHelper(private val stockChart: IStockChart) {

    // 缩放
    val xScaleMatrix by lazy { Matrix() }

    // 如果需要"一格一格"滑动，则用于缩放修正
    val fixXScaleMatrix by lazy { Matrix() }

    // 左右滑动
    val scrollMatrix by lazy { Matrix() }

    // 临时载体
    private val tmp2FloatArray by lazy { FloatArray(2) }
    private val tmp4FloatArray by lazy { FloatArray(4) }
    private val tmp9FloatArray by lazy { FloatArray(9) }
    private val tmpMatrix by lazy { Matrix() }

    private val scroller by lazy { OverScroller(stockChart.getContext()) }
    private var computeScrollCurrX = 0

    private var scalePx = 0f

    private var enableTriggerOnLoadMoreNextTime = true

    fun resetMatrix() {
        xScaleMatrix.reset()
        fixXScaleMatrix.reset()
        scrollMatrix.reset()
    }

    /**
     * 处理开始双指缩放
     */
    fun handleTouchScaleBegin(focusX: Float) {
        // 计算出缩放中心点对应的x逻辑坐标值
        tmp2FloatArray[0] = focusX - stockChart.getTouchArea().left
        tmp2FloatArray[1] = 0f
        stockChart.getChildCharts()[0].mapPointsReal2Value(tmp2FloatArray)
        val scaleFocusXValue = tmp2FloatArray[0]

        // 计算缩放阶段真正的缩放中心
        tmp2FloatArray[0] = scaleFocusXValue
        tmp2FloatArray[1] = 0f
        tmpMatrix.apply {
            reset()
            postConcat(stockChart.getChildCharts()[0].getCoordinateMatrix())
            postConcat(xScaleMatrix)
            postConcat(fixXScaleMatrix)
            mapPoints(tmp2FloatArray)
        }
        scalePx = tmp2FloatArray[0]
    }

    // 目前总共缩放了多少
    fun getTotalScaleX(): Float {
        xScaleMatrix.getValues(tmp9FloatArray)
        return tmp9FloatArray[Matrix.MSCALE_X]
    }

    /**
     * 处理双指缩放
     */
    fun handleTouchScale(scaleFactor: Float) {
        fixXScaleMatrix.reset()

        val totalScaleX = getTotalScaleX()

        // 计算在限制范围内要缩放多少
        var targetScaleFactor = when {
            totalScaleX * scaleFactor > stockChart.getConfig().scaleFactorMax -> {
                stockChart.getConfig().scaleFactorMax / totalScaleX
            }
            totalScaleX * scaleFactor < stockChart.getConfig().scaleFactorMin -> {
                stockChart.getConfig().scaleFactorMin / totalScaleX
            }
            else -> scaleFactor
        }
        xScaleMatrix.postScale(targetScaleFactor, 1f, scalePx, 0f)

        // 计算"一格"长度
        tmp4FloatArray[0] = 0f
        tmp4FloatArray[1] = 0f
        tmp4FloatArray[2] = 1f
        tmp4FloatArray[3] = 0f
        tmpMatrix.apply {
            reset()
            postConcat(stockChart.getChildCharts()[0].getCoordinateMatrix())
            postConcat(xScaleMatrix)
            mapPoints(tmp4FloatArray)
        }
        val lengthOfOneIndex = tmp4FloatArray[2] - tmp4FloatArray[0]

        val kEntitiesSize = stockChart.getConfig().getKEntitiesSize()

        if (lengthOfOneIndex * kEntitiesSize < stockChart.getTouchArea().right - stockChart.getTouchArea().left) {
            // 总内容缩小不能小于显示区域宽度
            val scaleX =
                (stockChart.getTouchArea().right - stockChart.getTouchArea().left) / (lengthOfOneIndex * kEntitiesSize)
            xScaleMatrix.postScale(scaleX, 1f, scalePx, 0f)
        } else if (!stockChart.getConfig().scrollSmoothly) {
            // 如果是"一格一格"地滑，则缩放使得显示范围内比例是"一格"的整数倍
            tmp4FloatArray[0] = 0f
            tmp4FloatArray[1] = 0f
            tmp4FloatArray[2] = 1f
            tmp4FloatArray[3] = 0f
            tmpMatrix.apply {
                reset()
                postConcat(stockChart.getChildCharts()[0].getCoordinateMatrix())
                postConcat(xScaleMatrix)
                mapPoints(tmp4FloatArray)
            }
            // 重新计算"一格"长度
            val lengthOfOneIndex = tmp4FloatArray[2] - tmp4FloatArray[0]
            val xRange =
                abs(stockChart.getChildCharts()[0].getChartMainDisplayArea().left - stockChart.getChildCharts()[0].getChartMainDisplayArea().right)
            if (xRange != 0f) {
                val scaleX = xRange / (xRange - xRange % lengthOfOneIndex)
                fixXScaleMatrix.postScale(scaleX, 1f, scalePx, 0f)
            }
        }

        stockChart.notifyChanged()
    }

    /**
     * 处理滑动
     */
    fun handleTouchScroll(distanceX: Float) {

        val kEntitiesSize = stockChart.getConfig().getKEntitiesSize()

        if (stockChart.getConfig().scrollAble
            && stockChart.getChildCharts().isNotEmpty()
            && kEntitiesSize > 0
        ) {
            // 计算滑动前的边界
            tmp4FloatArray[0] = 0f
            tmp4FloatArray[1] = 0f
            tmp4FloatArray[2] = kEntitiesSize.toFloat() // 多一个是因为边界要在最后一个点的右侧，要多加一个宽度
            tmp4FloatArray[3] = 0f
            tmpMatrix.apply {
                reset()
                postConcat(stockChart.getChildCharts()[0].getCoordinateMatrix())
                postConcat(xScaleMatrix)
                postConcat(fixXScaleMatrix)
                postConcat(scrollMatrix)
                mapPoints(tmp4FloatArray)
            }
            val limitLeft = tmp4FloatArray[0]
            val limitRight = tmp4FloatArray[2]

            // 显示区域
            val mainDisplayArea = stockChart.getChildCharts()[0].getChartMainDisplayArea()

            // 当前拖动的距离
            val dragDistanceX = -distanceX

            // 计算需要移动的距离
            var dx = 0f
            if (dragDistanceX > 0) { // 手指往右拖

                if (limitLeft + dragDistanceX > mainDisplayArea.left) {
                    var dragDistanceXLeft = dragDistanceX // 需要拖动的距离

                    if (limitLeft < mainDisplayArea.left) { // 处理未超出边界部分
                        dx += mainDisplayArea.left - limitLeft
                        dragDistanceXLeft -= dx
                    }

                    if (stockChart.getConfig().overScrollAble && dragDistanceXLeft > 0) { // 超出边界部分，处理回弹区
                        if (limitLeft - mainDisplayArea.left >= stockChart.getConfig().overScrollOnLoadMoreDistance) {
                            if (enableTriggerOnLoadMoreNextTime) {
                                enableTriggerOnLoadMoreNextTime = false
                                stockChart.dispatchOnLeftLoadMore()
                            }
                        }
                        dragDistanceXLeft *= stockChart.getConfig().frictionScrollExceedLimit // 加阻力
                        if (limitLeft + dragDistanceXLeft > mainDisplayArea.left + stockChart.getConfig().overScrollDistance) { // 超出极限
                            dragDistanceXLeft =
                                mainDisplayArea.left + stockChart.getConfig().overScrollDistance - limitLeft
                        }
                        dx += dragDistanceXLeft
                    } else {
                        if (enableTriggerOnLoadMoreNextTime) {
                            enableTriggerOnLoadMoreNextTime = false
                            stockChart.dispatchOnLeftLoadMore()
                        }
                    }
                } else {
                    dx = dragDistanceX
                    enableTriggerOnLoadMoreNextTime = true
                }

            } else { // 手指往左拖

                if (limitRight + dragDistanceX < mainDisplayArea.right) {
                    var dragDistanceXLeft = dragDistanceX // 需要拖动的距离

                    if (limitRight > mainDisplayArea.right) { // 未超出边界部分
                        dx += mainDisplayArea.right - limitRight
                        dragDistanceXLeft -= dx
                    }

                    if (stockChart.getConfig().overScrollAble && dragDistanceXLeft < 0) { // 超出边界部分，处理回弹区
                        if (mainDisplayArea.right - limitRight >= stockChart.getConfig().overScrollOnLoadMoreDistance) {
                            if (enableTriggerOnLoadMoreNextTime) {
                                enableTriggerOnLoadMoreNextTime = false
                                stockChart.dispatchOnRightLoadMore()
                            }
                        }
                        dragDistanceXLeft *= stockChart.getConfig().frictionScrollExceedLimit // 加阻力
                        if (limitRight + dragDistanceXLeft < mainDisplayArea.right - stockChart.getConfig().overScrollDistance) { // 超出极限
                            dragDistanceXLeft =
                                mainDisplayArea.right - stockChart.getConfig().overScrollDistance - limitRight
                        }
                        dx += dragDistanceXLeft
                    } else {
                        if (enableTriggerOnLoadMoreNextTime) {
                            enableTriggerOnLoadMoreNextTime = false
                            stockChart.dispatchOnRightLoadMore()
                        }
                    }
                } else {
                    dx = dragDistanceX
                    enableTriggerOnLoadMoreNextTime = true
                }
            }

            if (dx != 0f) {
                scrollMatrix.postTranslate(dx, 0f)
                stockChart.notifyChanged()
            }
        }
    }

    /**
     * 处理开始惯性滑动
     */
    fun handleFlingStart(
        velocityX: Float,
        velocityY: Float
    ) {

        val kEntitiesSize = stockChart.getConfig().getKEntitiesSize()

        if (stockChart.getConfig().scrollAble
            && stockChart.getChildCharts().isNotEmpty()
            && kEntitiesSize > 0
        ) {

            // 计算惯性滑动前边界位置
            tmp4FloatArray[0] = 0f
            tmp4FloatArray[1] = 0f
            tmp4FloatArray[2] = kEntitiesSize.toFloat() // 多一个是因为边界要在最后一个点的右侧，要多加一个宽度
            tmp4FloatArray[3] = 0f
            tmpMatrix.apply {
                reset()
                postConcat(stockChart.getChildCharts()[0].getCoordinateMatrix())
                postConcat(xScaleMatrix)
                postConcat(fixXScaleMatrix)
                postConcat(scrollMatrix)
                mapPoints(tmp4FloatArray)
            }
            val limitLeft = tmp4FloatArray[0]
            val limitRight = tmp4FloatArray[2]

            // 显示区域
            val mainDisplayArea = stockChart.getChildCharts()[0].getChartMainDisplayArea()

            if (limitRight < mainDisplayArea.right || limitLeft > mainDisplayArea.left) {
                // 边界外不能开始fling
                return
            }

            computeScrollCurrX = 0

            // 计算出x能惯性滑动的限制位置
            val minX = -abs(limitRight - mainDisplayArea.right)
            val maxX = abs(limitLeft - mainDisplayArea.left)


            if (velocityX < 0 && maxX != 0f || velocityX > 0 && minX != 0f) {
                val overX =
                    if (stockChart.getConfig().overScrollAble) stockChart.getConfig().overScrollDistance else 0
                var overY =
                    if (stockChart.getConfig().overScrollAble) stockChart.getConfig().overScrollDistance else 0

                scroller.fling(
                    computeScrollCurrX,
                    0,
                    velocityX.toInt(),
                    velocityY.toInt(),
                    minX.toInt(),
                    maxX.toInt(),
                    0,
                    0,
                    overX,
                    overY
                )

                stockChart.notifyChanged()
            }
        }
    }

    fun handleComputeScroll() {
        if (scroller.computeScrollOffset()) {

            // onLoadMore判断
            tmp4FloatArray[0] = 0f
            tmp4FloatArray[1] = 0f
            tmp4FloatArray[2] =
                stockChart.getConfig().getKEntitiesSize().toFloat() // 多一个是因为边界要在最后一个点的右侧，要多加一个宽度
            tmp4FloatArray[3] = 0f
            stockChart.getChildCharts()[0].mapPointsValue2Real(tmp4FloatArray)
            val limitLeft = tmp4FloatArray[0]
            val limitRight = tmp4FloatArray[2]
            val mainDisplayArea = stockChart.getChildCharts()[0].getChartMainDisplayArea()
            if (stockChart.getConfig().overScrollAble) {
                if (limitLeft - mainDisplayArea.left >= stockChart.getConfig().overScrollOnLoadMoreDistance) {
                    if (enableTriggerOnLoadMoreNextTime) {
                        enableTriggerOnLoadMoreNextTime = false
                        stockChart.dispatchOnLeftLoadMore()
                    }
                } else if (mainDisplayArea.right - limitRight >= stockChart.getConfig().overScrollOnLoadMoreDistance) {
                    if (enableTriggerOnLoadMoreNextTime) {
                        enableTriggerOnLoadMoreNextTime = false
                        stockChart.dispatchOnRightLoadMore()
                    }
                } else {
                    enableTriggerOnLoadMoreNextTime = true
                }
            } else {
                if (limitLeft.toInt() >= mainDisplayArea.left.toInt()) {
                    if (enableTriggerOnLoadMoreNextTime) {
                        enableTriggerOnLoadMoreNextTime = false
                        stockChart.dispatchOnLeftLoadMore()
                    }
                } else if (limitRight.toInt() <= mainDisplayArea.right.toInt()) {
                    if (enableTriggerOnLoadMoreNextTime) {
                        enableTriggerOnLoadMoreNextTime = false
                        stockChart.dispatchOnRightLoadMore()
                    }
                } else {
                    enableTriggerOnLoadMoreNextTime = true
                }
            }

            val distanceX = scroller.currX - computeScrollCurrX
            computeScrollCurrX = scroller.currX
            scrollMatrix.postTranslate(distanceX.toFloat(), 0f)
            stockChart.notifyChanged()
        }
    }

    /**
     * 检查如果移出了边界就需要回来
     */
    fun checkScrollBack() {
        val kEntitiesSize = stockChart.getConfig().getKEntitiesSize()

        // 计算此时的边界
        tmp4FloatArray[0] = 0f
        tmp4FloatArray[1] = 0f
        tmp4FloatArray[2] = kEntitiesSize.toFloat() // 多一个是因为边界要在最后一个点的右侧，要多加一个宽度
        tmp4FloatArray[3] = 0f
        stockChart.getChildCharts()[0].mapPointsValue2Real(tmp4FloatArray)
        val limitLeft = tmp4FloatArray[0]
        val limitRight = tmp4FloatArray[2]

        // 显示区域
        val mainDisplayArea = stockChart.getChildCharts()[0].getChartMainDisplayArea()

        var dx = 0
        if (limitLeft > mainDisplayArea.left) { // 左边界滑过头
            dx = (mainDisplayArea.left - limitLeft).toInt()
        } else if (limitRight < mainDisplayArea.right) { // 右边界滑过头
            dx = (mainDisplayArea.right - limitRight).toInt()
        }

        if (dx != 0) { // 需要回弹
            enableTriggerOnLoadMoreNextTime = false
            computeScrollCurrX = 0
            scroller.startScroll(0, 0, dx, 0)
            stockChart.notifyChanged()
        }
    }
}