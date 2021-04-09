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

package com.github.wangyiqian.stockchart.sample.sample1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.wangyiqian.stockchart.StockChartConfig
import com.github.wangyiqian.stockchart.childchart.kchart.KChartConfig
import com.github.wangyiqian.stockchart.childchart.kchart.KChartFactory
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarConfig
import com.github.wangyiqian.stockchart.childchart.timebar.TimeBarFactory
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