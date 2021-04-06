package com.github.wangyiqian.stockchart.sample.sample1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.wangyiqian.stockchart.StockChartConfig
import com.github.wangyiqian.stockchart.childchart.kchart.KChartConfig
import com.github.wangyiqian.stockchart.childchart.kchart.KChartFactory
import com.github.wangyiqian.stockchart.childchart.macdchart.MacdChartConfig
import com.github.wangyiqian.stockchart.childchart.macdchart.MacdChartFactory
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarConfig
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarFactory
import com.github.wangyiqian.stockchart.childchart.volumechart.VolumeChartConfig
import com.github.wangyiqian.stockchart.childchart.volumechart.VolumeChartFactory
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.sample.Data
import com.github.wangyiqian.stockchart.sample.R
import kotlinx.android.synthetic.main.activity_sample1.*

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/26
 */
class Sample1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample1)

        // 总配置
        val stockChartConfig = StockChartConfig()
        stock_chart.setConfig(stockChartConfig)

        // K线图的配置与工厂
        val kChartConfig = KChartConfig()
        val kChartFactory = KChartFactory(stockChart = stock_chart, childChartConfig = kChartConfig)

        // 时间条图的配置与工厂
        val timeBarConfig = TimeBarConfig()
        val timeBarFactory =
            TimeBarFactory(stockChart = stock_chart, childChartConfig = timeBarConfig)

        // 将需要显示的子图的工厂加入全局配置
        stockChartConfig.addChildCharts(kChartFactory, timeBarFactory)

        // 加载模拟数据
        Data.loadDayData(this, 0) { kEntities: List<IKEntity> ->

            // 初始显示最后50条数据
            val pageSize = 50

            // 设置加载到的数据
            stockChartConfig.setKEntities(
                kEntities,
                showStartIndex = kEntities.size - pageSize,
                showEndIndex = kEntities.size - 1
            )

            // 通知更新K线图
            stock_chart.notifyChanged()
        }

    }
}