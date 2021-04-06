package com.github.wangyiqian.stockchart.sample.custom

import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/9
 */
class CustomChartFactory(stockChart: IStockChart, childChartConfig: CustomChartConfig) :
    AbsChildChartFactory<CustomChartConfig>(stockChart, childChartConfig) {
    override fun createChart() = CustomChart(stockChart, childChartConfig)
}