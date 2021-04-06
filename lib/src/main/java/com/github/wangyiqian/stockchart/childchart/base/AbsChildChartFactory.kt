package com.github.wangyiqian.stockchart.childchart.base

import com.github.wangyiqian.stockchart.IStockChart

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/9
 */
abstract class AbsChildChartFactory<C : BaseChildChartConfig>(
    val stockChart: IStockChart,
    val childChartConfig: C
) {
    abstract fun createChart(): IChildChart

}