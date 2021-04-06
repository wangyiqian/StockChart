package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.IKEntity

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
interface ICalculator {
    fun calculate(param: String, input: List<IKEntity>): List<List<Float?>>
}