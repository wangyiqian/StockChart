package com.github.wangyiqian.stockchart.childchart.base

import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.util.NumberFormatUtil

/**
 * 高亮线配置
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/8
 */
class HighlightLabelConfig(
    // 背景色
    var bgColor: Int = DEFAULT_HIGHLIGHT_LABEL_BG_COLOR,
    // 背景圆角
    var bgCorner: Float = DEFAULT_HIGHLIGHT_LABEL_BG_CORNER,
    // 内间距
    var padding: Float = DEFAULT_HIGHLIGHT_LABEL_PADDING,
    // 文字大小
    var textSize: Float = DEFAULT_HIGHLIGHT_LABEL_TEXT_SIZE,
    // 文字颜色
    var textColor: Int = DEFAULT_HIGHLIGHT_LABEL_TEXT_COLOR,
    // 显示的内容格式化
    var textFormat: (value: Float) -> String = { value -> "${NumberFormatUtil.formatPrice(value)}" }
)