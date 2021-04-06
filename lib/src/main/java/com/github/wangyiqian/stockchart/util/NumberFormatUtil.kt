package com.github.wangyiqian.stockchart.util

import java.text.DecimalFormat

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/26
 */
object NumberFormatUtil {

    private val decimalFormat = DecimalFormat()

    @Synchronized
    fun formatPrice(price: Float): String {
        decimalFormat.maximumFractionDigits = 2
        decimalFormat.minimumFractionDigits = 2
        decimalFormat.groupingSize = 3
        return decimalFormat.format(price)
    }

}