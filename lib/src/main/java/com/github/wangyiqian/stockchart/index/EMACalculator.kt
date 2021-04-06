package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.entities.IKEntity

/**
 * 指数移动平均值（Exponential Moving Average）
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
object EMACalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val emaPeriodList = paramList.map { it.toInt() }

        val result = MutableList(emaPeriodList.size) { MutableList<Float?>(input.size) { 0f } }
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity is EmptyKEntity) {
                emaPeriodList.forEachIndexed { periodListIdx, _ ->
                    result[periodListIdx][kEntityIdx] = null
                }
                return@forEachIndexed
            }
            emaPeriodList.forEachIndexed { periodListIdx, n ->
                if (kEntityIdx == 0 || input[kEntityIdx - 1] is EmptyKEntity) {
                    result[periodListIdx][kEntityIdx] = input[kEntityIdx].getClosePrice()
                } else {
                    result[periodListIdx][kEntityIdx] =
                        2f / (n + 1) * input[kEntityIdx].getClosePrice() + (n - 1f) / (n + 1f) * result[periodListIdx][kEntityIdx - 1]!!
                }
            }
        }
        return result
    }
}