- [介绍](#%E4%BB%8B%E7%BB%8D)
    - [特点](#%E7%89%B9%E7%82%B9)
- [效果图](#%E6%95%88%E6%9E%9C%E5%9B%BE)
- [插一条内推广告](#%E6%8F%92%E4%B8%80%E6%9D%A1%E5%86%85%E6%8E%A8%E5%B9%BF%E5%91%8A)
- [用法](#%E7%94%A8%E6%B3%95)
    - [先明白几个概念](#%E5%85%88%E6%98%8E%E7%99%BD%E5%87%A0%E4%B8%AA%E6%A6%82%E5%BF%B5)
    - [基本使用](#%E5%9F%BA%E6%9C%AC%E4%BD%BF%E7%94%A8)
      - [1. 集成](#1-%E9%9B%86%E6%88%90)
      - [2. 布局文件加入StockChart布局](#2-%E5%B8%83%E5%B1%80%E6%96%87%E4%BB%B6%E5%8A%A0%E5%85%A5stockchart%E5%B8%83%E5%B1%80)
      - [3. 设置`StockChart`的配置](#3-%E8%AE%BE%E7%BD%AEstockchart%E7%9A%84%E9%85%8D%E7%BD%AE)
      - [4. 添加子图：将需要的子图的工厂添加给全局配置即可，如以下将添加三个子图：K线图、时间条、MACD指标图](#4-%E6%B7%BB%E5%8A%A0%E5%AD%90%E5%9B%BE%E5%B0%86%E9%9C%80%E8%A6%81%E7%9A%84%E5%AD%90%E5%9B%BE%E7%9A%84%E5%B7%A5%E5%8E%82%E6%B7%BB%E5%8A%A0%E7%BB%99%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AE%E5%8D%B3%E5%8F%AF%E5%A6%82%E4%BB%A5%E4%B8%8B%E5%B0%86%E6%B7%BB%E5%8A%A0%E4%B8%89%E4%B8%AA%E5%AD%90%E5%9B%BEk%E7%BA%BF%E5%9B%BE%E6%97%B6%E9%97%B4%E6%9D%A1macd%E6%8C%87%E6%A0%87%E5%9B%BE)
      - [5. 将K线数据传给全局配置](#5-%E5%B0%86k%E7%BA%BF%E6%95%B0%E6%8D%AE%E4%BC%A0%E7%BB%99%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AE)
    - [使用进阶](#%E4%BD%BF%E7%94%A8%E8%BF%9B%E9%98%B6)
    - [所有配置](#%E6%89%80%E6%9C%89%E9%85%8D%E7%BD%AE)
        - [全局配置`StockChartConfig`](#%E5%85%A8%E5%B1%80%E9%85%8D%E7%BD%AEstockchartconfig)
        - [K线图配置`KChartConfig`](#k%E7%BA%BF%E5%9B%BE%E9%85%8D%E7%BD%AEkchartconfig)
        - [时间条图配置`TimeBarConfig`](#%E6%97%B6%E9%97%B4%E6%9D%A1%E5%9B%BE%E9%85%8D%E7%BD%AEtimebarconfig)
        - [成交量图配置`VolumeChartConfig`](#%E6%88%90%E4%BA%A4%E9%87%8F%E5%9B%BE%E9%85%8D%E7%BD%AEvolumechartconfig)
        - [MACD指标图配置`MacdChartConfig`](#macd%E6%8C%87%E6%A0%87%E5%9B%BE%E9%85%8D%E7%BD%AEmacdchartconfig)
        - [KDJ指标图配置`KdjChartConfig`](#kdj%E6%8C%87%E6%A0%87%E5%9B%BE%E9%85%8D%E7%BD%AEkdjchartconfig)
        - [长按时高亮线的标签配置`HighlightLabelConfig`](#%E9%95%BF%E6%8C%89%E6%97%B6%E9%AB%98%E4%BA%AE%E7%BA%BF%E7%9A%84%E6%A0%87%E7%AD%BE%E9%85%8D%E7%BD%AEhighlightlabelconfig)
        - [K线图标签配置`LabelConfig`](#k%E7%BA%BF%E5%9B%BE%E6%A0%87%E7%AD%BE%E9%85%8D%E7%BD%AElabelconfig)
    - [如何自定义子图](#%E5%A6%82%E4%BD%95%E8%87%AA%E5%AE%9A%E4%B9%89%E5%AD%90%E5%9B%BE)
        - [三步走](#%E4%B8%89%E6%AD%A5%E8%B5%B0)
        - [示例](#%E7%A4%BA%E4%BE%8B)
- [示例APK下载](#%E7%A4%BA%E4%BE%8Bapk%E4%B8%8B%E8%BD%BD)
- [反馈](#%E5%8F%8D%E9%A6%88)
- [请作者喝杯咖啡呗](#%E8%AF%B7%E4%BD%9C%E8%80%85%E5%96%9D%E6%9D%AF%E5%92%96%E5%95%A1%E5%91%97)
- [Licenses](#licenses)

[![](https://jitpack.io/v/wangyiqian/StockChart.svg)](https://jitpack.io/#wangyiqian/StockChart)

# 介绍
StockChart是一款适用于Android的高扩展性、高性能股票图开发库，轻松完成各种子图的组合，还能灵活的定制自己的子图满足复杂的业务需求。
### 特点
* 内置子图：K线图、时间条图、成交量图、MACD指标图、KDJ指标图
* 内置指标：MA（移动平均线）、EMA（指数移动平均值）、BOLL（布林线）、MACD（指数平滑异同移动平均线）、KDJ（随机指标）
* **支持自定义子图**。若内置的子图样式与配置无法满足您的需求或者您需要更多类型的指标图时，您可以通过自定义子图的方式来实现自己的子图。您甚至可以把内置的所有子图都当做你实现自定义子图的参考示例。**这种设计可以避免基于源码修改，满足开闭原则**。

# 效果图
<img src=img/sample.gif width=60% />
(若图片未显示，可能需要科学上网。)

# 插一条内推广告
**阿里巴巴** 、**老虎集团（老虎证券）** 2021各岗位内推可发简历到我邮箱:wangyiqian9891@gmail.com

# 用法
### 先明白几个概念
* 所有要显示的图（内置的子图与自定义的子图）都是`StockChart`的子图。内置的子图：`KChart`（K线图）、`TimeBarChart`（时间条图）、`VolumeChart`（成交量图）、`MacdChart`（MACD指标图）、`KdjChart`（KDJ指标图）
* 任何变化（如K线数据增加）都是通过修改配置去更新。全局配置：`StockChartConfig`，每个子图也有自己的配置如：`KChartConfig`、`KDJChartConfig`。
* 逻辑坐标：原点是左下角，x轴从左到右变大，y轴从下到上变大。是最接近普通人理解股票图的坐标。
    * 逻辑坐标的x轴：固定规则，是这个数据集的下标，即从0开始，刻度为1。最小最大值范围是[0, kEntities.size - 1]。
    * 逻辑坐标的y轴：由子类各自根据实际情况提供。比如成交量图每个数据对应y轴上的值应该是成交量，比如K线图每个数据对应y轴上的值应该是价格。
* 实际坐标：就是Android View的坐标体系，即最终绘制时View认识的坐标，原点是左上角，x轴从左到右变大，y轴从上到下变大。

### 基本使用
#### 1. 集成
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
```groovy
dependencies {
    implementation 'com.github.wangyiqian:StockChart:1.0.1'
}
```
#### 2. 布局文件加入StockChart布局
```xml
<com.github.wangyiqian.stockchart.StockChart
    android:id="@+id/stock_chart"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```
#### 3. 设置`StockChart`的配置
```kotlin
val stockChartConfig = StockChartConfig()
stock_chart.setConfig(stockChartConfig)
```
#### 4. 添加子图：将需要的子图的工厂添加给全局配置即可，如以下将添加三个子图：K线图、时间条、MACD指标图
```kotlin
// K线图的配置与工厂
val kChartConfig = KChartConfig()
val kChartFactory = KChartFactory(stockChart = stock_chart, childChartConfig = kChartConfig)

// 时间条图的配置与工厂
val timeBarConfig = TimeBarConfig()
val timeBarFactory = TimeBarFactory(stockChart = stock_chart, childChartConfig = timeBarConfig)

// MACD指标图的配置与工厂
val macdChartConfig = MacdChartConfig()
val macdChartFactory = MacdChartFactory(stockChart = stock_chart, childChartConfig = macdChartConfig)

// 将需要显示的子图的工厂加入全局配置
stockChartConfig.addChildCharts(kChartFactory, timeBarFactory, macdChartFactory)
```
#### 5. 将K线数据传给全局配置
```kotlin
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
```
> 注意：任何配置的修改都需要调用`StockChart`的`notifyChanged()`方法去更新视图展示

### 使用进阶
详细请参考提供的示例（samples模块）

### 所有配置
##### 全局配置`StockChartConfig`
|字段|描述|
|---|---|
|kEntities|K线数据|
|showStartIndex|初始显示区域的起始坐标|
|showEndIndex|初始显示区域的结束坐标|
|scrollAble|是否支持滑动|
|overScrollAble|是否支持"滑过头回弹"效果|
|overScrollDistance|"滑过头回弹"最大距离|
|overScrollOnLoadMoreDistance|"滑过头回弹"过程中触发加载更多需要的距离|
|scaleAble|是否支持双指缩放|
|scrollSmoothly|是否需要"平滑的"滑动。如果false，滑动时一个下标对应的内容要么全显示，要么不显示。|
|frictionScrollExceedLimit|超出滑动限制范围时拖动的"摩擦力"|
|scaleFactorMax|双指缩放最大缩放比例|
|scaleFactorMin|双指缩放最小缩放比例|
|showHighlightHorizontalLine|是否支持长按高亮横线|
|highlightHorizontalLineWidth|长按高亮横线宽度|
|highlightHorizontalLineColor|长按高亮横线颜色|
|showHighlightVerticalLine|是否支持长按高亮竖线|
|highlightVerticalLineWidth|长按高亮竖线宽度|
|highlightVerticalLineColor|长按高亮竖线颜色|
|riseColor|涨色值|
|downColor|跌色值|
|backgroundColor|背景色|
|gridHorizontalLineCount|背景网格横线数|
|gridVerticalLineCount|背景网格竖线数|
|gridLineColor|背景网格线条色|
|gridLineStrokeWidth|背景网格线条宽度|

##### K线图配置`KChartConfig`
|字段|描述|
|---|---|
|height|高度|
|marginTop|顶部外间距|
|marginBottom|底部外间距|
|onHighlightListener|长按回调|
|chartMainDisplayAreaPaddingTop|主数据显示区域的顶部内间距|
|chartMainDisplayAreaPaddingBottom|主数据显示区域的底部内间距|
|highlightLabelLeft|长按时高亮线左侧标签配置|
|highlightLabelTop|长按时高亮线顶部标签配置|
|highlightLabelRight|长按时高亮线右侧标签配置|
|highlightLabelBottom|长按时高亮线底部标签配置|
|lineChartColor|线形图的线条颜色|
|lineChartStrokeWidth|线形图的线条宽度|
|mountainChartColor|山峰图线条颜色|
|mountainChartStrokeWidth|山峰图的线条宽度|
|mountainChartLinearGradientColors|山峰图的封闭渐变色|
|candleChartLineStrokeWidth|蜡烛图的中间线宽度|
|hollowChartLineStrokeWidth|空心蜡烛图线条宽度|
|barChartLineStrokeWidth|美国线图（竹节图）线条宽度|
|costPrice|成本线价格|
|costPriceLineColor|成本线颜色|
|costPriceLineWidth|成本线宽度|
|indexStrokeWidth|指标线条宽度|
|barSpaceRatio|柱子之间的空间占比柱子宽度|
|index|需要展示的指标类型|
|indexColors|指标线的颜色|
|leftLabelConfig|左侧标签配置|
|rightLabelConfig|右侧标签配置|

##### 时间条图配置`TimeBarConfig`
|字段|描述|
|---|---|
|height|高度|
|marginTop|顶部外间距|
|marginBottom|底部外间距|
|onHighlightListener|长按回调|
|chartMainDisplayAreaPaddingTop|主数据显示区域的顶部内间距|
|chartMainDisplayAreaPaddingBottom|主数据显示区域的底部内间距|
|backGroundColor|背景色|
|labelTextSize|标签文本大小|
|labelTextColor|标签文本色|
|highlightLabelTextSize|长按显示的标签文本大小|
|highlightLabelTextColor|长按显示的标签文本色|
|highlightLabelBgColor|长按显示的标签背景色|
|type|时间条样式|

##### 成交量图配置`VolumeChartConfig`
|字段|描述|
|---|---|
|height|高度|
|marginTop|顶部外间距|
|marginBottom|底部外间距|
|onHighlightListener|长按回调|
|chartMainDisplayAreaPaddingTop|主数据显示区域的顶部内间距|
|chartMainDisplayAreaPaddingBottom|主数据显示区域的底部内间距|
|highlightLabelLeft|长按时高亮线左侧标签配置|
|highlightLabelRight|长按时高亮线右侧标签配置|
|barSpaceRatio|柱子之间的空间占比柱子宽度|

##### MACD指标图配置`MacdChartConfig`
|字段|描述|
|---|---|
|height|高度|
|marginTop|顶部外间距|
|marginBottom|底部外间距|
|onHighlightListener|长按回调|
|chartMainDisplayAreaPaddingTop|主数据显示区域的顶部内间距|
|chartMainDisplayAreaPaddingBottom|主数据显示区域的底部内间距|
|highlightLabelLeft|长按时高亮线左侧标签配置|
|highlightLabelRight|长按时高亮线右侧标签配置|
|difLineColor|dif线颜色|
|difLineStrokeWidth|dif线宽度|
|deaLineColor|dea线颜色|
|deaLineStrokeWidth|dea线宽度|
|macdTextColor|macd文字颜色|
|barSpaceRatio|柱子之间的空间占比柱子宽度|
|index|需要展示的指标配置|

##### KDJ指标图配置`KdjChartConfig`
|字段|描述|
|---|---|
|height|高度|
|marginTop|顶部外间距|
|marginBottom|底部外间距|
|onHighlightListener|长按回调|
|chartMainDisplayAreaPaddingTop|主数据显示区域的顶部内间距|
|chartMainDisplayAreaPaddingBottom|主数据显示区域的底部内间距|
|highlightLabelLeft|长按时高亮线左侧标签配置|
|highlightLabelRight|长按时高亮线右侧标签配置|
|kLineColor|k线颜色|
|kLineStrokeWidth|k线宽度|
|dLineColor|d线颜色|
|dLineStrokeWidth|d线宽度|
|jLineColor|j线颜色|
|jLineStrokeWidth|j线宽度|
|index|需要展示的指标配置|

##### 长按时高亮线的标签配置`HighlightLabelConfig`
|字段|描述|
|---|---|
|bgColor|背景色|
|bgCorner|背景圆角|
|padding|内间距|
|textSize|文字大小|
|textColor|文字颜色|
|textFormat|显示的内容格式化|

##### K线图标签配置`LabelConfig`
|字段|描述|
|---|---|
|count|标签数|
|formatter|显示内容格式化|
|textSize|文字大小|
|textColor|文字颜色|
|horizontalMargin|水平外间距|
|marginTop|顶部外间距|
|marginBottom|底部外间距|


### 如何自定义子图
##### 三步走
1. 提供子图的配置类，用于定义子图的各种配置，需要继承`BaseChildChartConfig`
2. 提供子图的UI类，用于绘制UI细节，需要继承`BaseChildChart`
3. 提供子图的工厂类，用于实例化子图，需要继承`AbsChildChartFactory`
##### 示例
```kotlin
class CustomChartConfig(  
    height: Int = DEFAULT_CHILD_CHART_HEIGHT,  
  marginTop: Int = DEFAULT_CHILD_CHART_MARGIN_TOP,  
  marginBottom: Int = DEFAULT_CHILD_CHART_MARGIN_BOTTOM,  
  onHighlightListener: OnHighlightListener? = null,  
  chartMainDisplayAreaPaddingTop: Float = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,  
  chartMainDisplayAreaPaddingBottom: Float = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,  
 var bigLabel: String? = null  
) : BaseChildChartConfig(  
    height,  
  marginTop,  
  marginBottom,  
  onHighlightListener,  
  chartMainDisplayAreaPaddingTop,  
  chartMainDisplayAreaPaddingBottom  
)
```
```kotlin
class CustomChart(
    stockChart: IStockChart,
    chartConfig: CustomChartConfig
) : BaseChildChart<CustomChartConfig>(stockChart, chartConfig) {
    
    override fun onKEntitiesChanged() {
        // 如果需要的话，在这里处理K先数据变化后要做的事  
    }

    override fun getYValueRange(startIndex: Int, endIndex: Int, result: FloatArray) {
        // 提供指定下标范围内[startIndex ~ endIndex]，y轴逻辑坐标的范围值  
    }  

    override fun preDrawBackground(canvas: Canvas) {
        // ... 绘制细节 
    }

    override fun drawBackground(canvas: Canvas) {
        // ... 绘制细节  
    }

    override fun preDrawData(canvas: Canvas) {
        // ... 绘制细节
    }

    override fun drawData(canvas: Canvas) {
        // ... 绘制细节
    }

    override fun preDrawHighlight(canvas: Canvas) {
        // ... 绘制细节 
    }

    override fun drawHighlight(canvas: Canvas) {
        // ... 绘制细节 
    }

    override fun drawAddition(canvas: Canvas) {
        // ... 绘制细节 
    }
}
```
```kotlin
class CustomChartFactory(stockChart: IStockChart, childChartConfig: CustomChartConfig) :  
    AbsChildChartFactory<CustomChartConfig>(stockChart, childChartConfig) {  
    override fun createChart() = CustomChart(stockChart, childChartConfig)  
}
```
```kotlin
// 自定义子图的使用

// 自定义子图的配置与工厂
val customChartConfig = CustomChartConfig()
val customChartFactory = CustomChartFactory(stock_chart, customChartConfig)
// 添加子图
stockChartConfig.addChildCharts(customChartFactory)
// 更新UI
stock_chart.notifyChanged()
```
* 详细请参考samples模块，里面有自定义子图的示例。

# 示例APK下载
![StockChartApk.png](img/StockChartApk.png)

http://yiqian.wang:8081/stock_chart.apk

# 反馈
感觉好用就点个Star呗~感激万分。

任何问题欢迎在Issues区提问，或者直接联系我。

我的邮箱：wangyiqian9891@gmail.com

# 请作者喝杯咖啡呗
您的支持是我最大的动力！
![donate.jpg](img/donate.jpg)

# Licenses
```
Copyright 2021 WangYiqian

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
```