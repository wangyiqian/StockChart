package com.github.wangyiqian.stockchart.childchart.base

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import android.view.View
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
}