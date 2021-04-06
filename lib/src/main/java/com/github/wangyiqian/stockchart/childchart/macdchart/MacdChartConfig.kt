package com.github.wangyiqian.stockchart.childchart.macdchart

import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.childchart.base.*
import com.github.wangyiqian.stockchart.index.Index
import com.github.wangyiqian.stockchart.listener.OnHighlightListener

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
class MacdChartConfig(
    height: Int = DEFAULT_CHILD_CHART_HEIGHT,
    marginTop: Int = DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_TOP,
    chartMainDisplayAreaPaddingBottom: Float = DEFAULT_CHART_MAIN_DISPLAY_AREA_PADDING_BOTTOM,
    // 长按时高亮线左侧标签配置
    var highlightLabelLeft: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
    var highlightLabelRight: HighlightLabelConfig? = null,
    // dif线颜色
    var difLineColor: Int = DEFAULT_MACD_DIF_LINE_COLOR,
    // dif线宽度
    val difLineStrokeWidth: Float = DEFAULT_MACD_DIF_LINE_STROKE_WIDTH,
    // dea线颜色
    var deaLineColor: Int = DEFAULT_MACD_DEA_LINE_COLOR,
    // dea线宽度
    val deaLineStrokeWidth: Float = DEFAULT_MACD_DEA_LINE_STROKE_WIDTH,
    // macd文字颜色
    val macdTextColor: Int = DEFAULT_MACD_TEXT_COLOR,
    // 柱子之间的空间占比柱子宽度
    var barSpaceRatio: Float = DEFAULT_MACD_BAR_SPACE_RATIO,
    // 需要展示的指标配置
    var index: Index? = DEFAULT_MACD_INDEX
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)