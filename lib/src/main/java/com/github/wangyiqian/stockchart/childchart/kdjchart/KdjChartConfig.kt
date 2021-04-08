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

package com.github.wangyiqian.stockchart.childchart.kdjchart

import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.childchart.base.*
import com.github.wangyiqian.stockchart.index.Index
import com.github.wangyiqian.stockchart.listener.OnHighlightListener

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
class KdjChartConfig(
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
    // k线颜色
    var kLineColor: Int = DEFAULT_KDJ_K_LINE_COLOR,
    // k线宽度
    var kLineStrokeWidth: Float = DEFAULT_KDJ_K_LINE_STROKE_WIDTH,
    // d线颜色
    var dLineColor: Int = DEFAULT_KDJ_D_LINE_COLOR,
    // d线宽度
    var dLineStrokeWidth: Float = DEFAULT_KDJ_D_LINE_STROKE_WIDTH,
    // j线颜色
    var jLineColor: Int = DEFAULT_KDJ_J_LINE_COLOR,
    // j线宽度
    var jLineStrokeWidth: Float = DEFAULT_KDJ_J_LINE_STROKE_WIDTH,
    // 需要展示的指标配置
    var index: Index? = DEFAULT_KDJ_INDEX
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
)