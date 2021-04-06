package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.entities.IKEntity

/**
 * 移动平均线（Moving Average）
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/14
 */
object MACalculator: ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val periodList = paramList.map { it.toInt() }

        val result = MutableList(periodList.size) { MutableList<Float?>(input.size) { 0f } }
        val pFromList = MutableList(periodList.size) { 0 }
        val pEndList = MutableList(periodList.size) { 0 }
        val sumList = MutableList(periodList.size) { 0f }
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity is EmptyKEntity) {
                periodList.forEachIndexed { periodListIdx, _ ->
                    result[periodListIdx][kEntityIdx] = null
                    sumList[periodListIdx] = 0f
                }
                return@forEachIndexed
            }

            periodList.forEachIndexed { periodListIdx, period ->
                if (kEntityIdx == 0 || input[kEntityIdx - 1] is EmptyKEntity) {
                    pFromList[periodListIdx] = kEntityIdx
                }

                pEndList[periodListIdx] = kEntityIdx

                sumList[periodListIdx] += kEntity.getClosePrice()

                if (pEndList[periodListIdx] - pFromList[periodListIdx] + 1 == period) {
                    result[periodListIdx][kEntityIdx] = sumList[periodListIdx] / period

                    sumList[periodListIdx] -= input[pFromList[periodListIdx]].getClosePrice()

                    pFromList[periodListIdx] += 1
                } else {
                    result[periodListIdx][kEntityIdx] = null
                }
            }
        }
        return result
    }
}