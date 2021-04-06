package com.github.wangyiqian.stockchart.childchart.base

import com.github.wangyiqian.stockchart.listener.OnHighlightListener

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/7
 */
abstract class BaseChildChartConfig(
    height: Int,
    marginTop: Int,
    marginBottom: Int,
    var onHighlightListener: OnHighlightListener?,
    /**
     * 主数据显示区域的顶部内间距
     */
    val chartMainDisplayAreaPaddingTop: Float,
    /**
     * 主数据显示区域的底部内间距
     */
    val chartMainDisplayAreaPaddingBottom: Float
) {
    var height: Int = 0
        set(value) {
            setSizeFlag = true
            field = value
        }

    var marginTop: Int = 0
        set(value) {
            setMarginFlag = true
            field = value
        }

    var marginBottom: Int = 0
        set(value) {
            setMarginFlag = true
            field = value
        }

    var setSizeFlag = false

    var setMarginFlag = false

    init {
        this.height = height
        this.marginTop = marginTop
        this.marginBottom = marginBottom
    }

}