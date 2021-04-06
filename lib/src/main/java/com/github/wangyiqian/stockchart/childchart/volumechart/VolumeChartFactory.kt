package com.github.wangyiqian.stockchart.childchart.volumechart

import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.StockChart
import com.github.wangyiqian.stockchart.childchart.base.AbsChildChartFactory
import com.github.wangyiqian.stockchart.childchart.base.IChildChart

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/9
 */
class VolumeChartFactory(stockChart: IStockChart, childChartConfig: VolumeChartConfig) :
    AbsChildChartFactory<VolumeChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = VolumeChart(stockChart, childChartConfig)
}