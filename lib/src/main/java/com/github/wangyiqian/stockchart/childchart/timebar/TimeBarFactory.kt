package com.github.wangyiqian.stockchart.childchart.timebar

import com.github.wangyiqian.stockchart.IStockChart
import com.github.wangyiqian.stockchart.childchart.base.AbsChildChartFactory

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/22
 */
class TimeBarFactory(stockChart: IStockChart, childChartConfig: TimeBarConfig) :
    AbsChildChartFactory<TimeBarConfig>(stockChart, childChartConfig) {
    override fun createChart() = TimeBar(stockChart, childChartConfig)
}