package com.github.wangyiqian.stockchart.childchart.kdjchart

import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.AbsChildChartFactory
import com.github.wangyiqian.stockchart.childchart.base.IChildChart

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
class KdjChartFactory(
    stockChart: IStockChart,
    childChartConfig: KdjChartConfig
) : AbsChildChartFactory<KdjChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = KdjChart(stockChart, childChartConfig)
}