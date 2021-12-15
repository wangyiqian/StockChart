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

package com.github.wangyiqian.stockchart.childchart.volumechart

import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.childchart.base.*
import com.github.wangyiqian.stockchart.listener.OnHighlightListener

/**
 * 成交量图配置
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/7
 */
class VolumeChartConfig(
    height: Int = DEFAULT_CHILD_CHART_HEIGHT,
    marginTop: Int = DEFAULT_CHILD_CHART_MARGIN_TOP,
    marginBottom: Int = DEFAULT_CHILD_CHART_MARGIN_BOTTOM,
    onHighlightListener: OnHighlightListener? = null,
    chartMainDisplayAreaPaddingTop: Float = 0f,
    chartMainDisplayAreaPaddingBottom: Float = 0f,
    // 柱子样式
    var volumeChartType: VolumeChartType = VolumeChartType.CANDLE(),
    // 长按时高亮线左侧标签配置
    var highlightLabelLeft: HighlightLabelConfig? = null,
    // 长按时高亮线右侧标签配置
    var highlightLabelRight: HighlightLabelConfig? = null,
    // 柱子之间的空间占比柱子宽度
    var barSpaceRatio: Float = DEFAULT_VOLUME_BAR_SPACE_RATIO,
    // 柱子空心时的线条宽度
    var hollowChartLineStrokeWidth: Float = DEFAULT_VOLUME_CHART_HOLLOW_CHART_LINE_STROKE_WIDTH
) : BaseChildChartConfig(
    height,
    marginTop,
    marginBottom,
    onHighlightListener,
    chartMainDisplayAreaPaddingTop,
    chartMainDisplayAreaPaddingBottom
) {

    sealed class VolumeChartType {
        // 实心
        class CANDLE : VolumeChartType()
        // 空心
        class HOLLOW : VolumeChartType()
    }

}