package com.github.wangyiqian.stockchart.listener

import com.github.wangyiqian.stockchart.entities.Highlight

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/4/3
 */
interface OnHighlightListener {
    fun onHighlightBegin()
    fun onHighlightEnd()
    fun onHighlight(highlight: Highlight)
}