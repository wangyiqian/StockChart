package com.github.wangyiqian.stockchart.childchart.rskchart

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.childchart.base.BaseChildChartConfig
import com.github.wangyiqian.stockchart.childchart.base.HighlightLabelConfig
import com.github.wangyiqian.stockchart.index.Index
import com.github.wangyiqian.stockchart.listener.OnHighlightListener

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2023/3/9
 */
class RsiChartConfig(
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
    // 指标线的颜色
    var indexColors: List<Int> = listOf(
        Color.parseColor("#F5EC58"),
        Color.parseColor("#FF7CE5"),
        Color.parseColor("#9EC7FE"),
        Color.parseColor("#fb0606"),
        Color.parseColor("#a003fa"),
        Color.parseColor("#02cefa"),
        Color.parseColor("#02fa88"),
        Color.parseColor("#fa6b02")
    ),
    // 指标线宽度
    var lineStrokeWidth: Float = 3f,
    // 虚线颜色
    var dashLineColor: Int = Color.LTGRAY,
    // 需要展示的指标配置
    var index: Index? = Index.RSI(),
    // 指标头文字背景色
    var indexStarterBgColor: Int = Color.TRANSPARENT,
    // 指标头文字背景水平内间距
    var indexStarterBgPaddingHorizontal: Float = 0f,
    // 指标头文字右侧图标
    var indexStarterRightIcon: Bitmap? = null,
    // 指标头文字点击事件
    var indexStarterClickListener: ((View) -> Unit)? = null
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)