package com.github.wangyiqian.stockchart.sample

import com.github.wangyiqian.stockchart.util.NumberFormatUtil
import java.text.DecimalFormat

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/4/4
 */
object Util {

    private val numberDecimalFormat = DecimalFormat().apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    /**
     * 格式化成交量
     */
    fun formatVolume(volume: Long): String {
        var magnitude = 1f
        var unit = ""
        when {
            volume > 1_0000_0000_0000f -> {
                magnitude = 1_0000_0000_0000f
                unit = "万亿"
            }
            volume > 1_0000_0000f -> {
                magnitude = 1_0000_0000f
                unit = "亿"
            }
            volume > 1_0000f -> {
                magnitude = 1_0000f
                unit = "万"
            }
            else -> {
                magnitude = 1f
                unit = ""
            }
        }

        return "${numberDecimalFormat.format(volume / magnitude)}${unit}股"
    }

    /**
     * 格式化涨跌幅
     */
    fun formatChangeRatio(new: Float, old: Float): String {
        if (old == 0f) return "——"
        val ratio = (new - old) / old * 100
        return "${if (new > old) "+" else ""}${numberDecimalFormat.format(ratio)}"
    }

}