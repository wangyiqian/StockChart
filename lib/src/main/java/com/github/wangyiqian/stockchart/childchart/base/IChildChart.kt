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

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.github.wangyiqian.stockchart.entities.GestureEvent
import com.github.wangyiqian.stockchart.entities.Highlight
import com.github.wangyiqian.stockchart.entities.IKEntity

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/1
 */
interface IChildChart {
    fun view(): View

    fun invalidate()

    /**
     * 获取长按高亮信息
     */
    fun getHighlight(): Highlight?

    /**
     * 获取
     */
    fun getKEntities(): List<IKEntity>

    /**
     * 获取显示区域
     */
    fun getChartDisplayArea(): RectF

    /**
     * 获取主显示区域
     */
    fun getChartMainDisplayArea(): RectF

    fun getCoordinateMatrix(): Matrix

    /**
     * 坐标点从逻辑坐标转换成实际坐标
     */
    fun mapPointsValue2Real(pts: FloatArray)

    /**
     * 矩形从逻辑坐标转换成实际坐标
     */
    fun mapRectValue2Real(rect: RectF)

    /**
     * 路径从逻辑坐标转换成实际坐标
     */
    fun mapPathValue2Real(path: Path)

    /**
     * 坐标点从实际坐标转换成逻辑坐标
     */
    fun mapPointsReal2Value(pts: FloatArray)

    /**
     * 矩形从实际坐标转换成逻辑坐标
     */
    fun mapRectReal2Value(rect: RectF)

    /**
     * 路径从实际坐标转换成逻辑坐标
     */
    fun mapPathReal2Value(path: Path)

    /**
     * 获取对应配置
     */
    fun getConfig(): BaseChildChartConfig

    /**
     * 点击
     */
    fun onTap(event: GestureEvent)

    /**
     * 涨跌判断
     */
    fun isRise(idx: Int): Boolean

    fun getHighlightValue(highlightX: Float, highlightY: Float, highlightValue: FloatArray) {
        highlightValue[0] = highlightX
        highlightValue[1] = highlightY
        mapPointsReal2Value(highlightValue)
    }
}