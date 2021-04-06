package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.entities.IKEntity
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * BOLL 布林线
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/16
 */
object BollCalculator: ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val n = paramList[0].toInt()
        val k = paramList[1].toInt()

        // 1. MB 2.UP 3.DN
        val result = MutableList(3) { MutableList<Float?>(input.size) { 0f } }
        val mbIdx = 0
        val upIdx = 1
        val dnIdx = 2

        var pFrom = 0
        var pEnd = 0
        var sum = 0f
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity is EmptyKEntity) {
                result[mbIdx][kEntityIdx] = null
                result[upIdx][kEntityIdx] = null
                result[dnIdx][kEntityIdx] = null
                sum = 0f
                return@forEachIndexed
            }

            if (kEntityIdx == 0 || input[kEntityIdx - 1] is EmptyKEntity) {
                pFrom = kEntityIdx
            }

            pEnd = kEntityIdx

            sum += kEntity.getClosePrice()

            if (pEnd - pFrom + 1 == n) {
                val ma = sum / n
                result[mbIdx][kEntityIdx] = ma
                sum -= input[pFrom].getClosePrice()
                pFrom += 1

                var squareSum = 0f
                for (i in pEnd downTo pEnd - n + 1) {
                    squareSum += (input[i].getClosePrice() - ma).pow(2)
                }
                val std = sqrt(squareSum / n)
                result[upIdx][kEntityIdx] = ma + k * std
                result[dnIdx][kEntityIdx] = ma - k * std
            } else {
                result[mbIdx][kEntityIdx] = null
                result[upIdx][kEntityIdx] = null
                result[dnIdx][kEntityIdx] = null
            }
        }
        return result
    }

}