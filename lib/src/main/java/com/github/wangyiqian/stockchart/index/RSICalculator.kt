package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.entities.containFlag
import kotlin.math.max
import kotlin.math.min

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2023/3/9
 */
object RSICalculator : ICalculator {
    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val periodList = try {
            paramList.map { it.toInt() }
        } catch (tr: Throwable) {
            emptyList<Int>()
        }
        val result = MutableList(periodList.size) { MutableList<Float?>(input.size) { 0f } }
        val preAvgRiseList = MutableList(periodList.size) { 0f }
        val preAvgDownList = MutableList(periodList.size) { 0f }
        var preNotEmptyEntityCount = 0
        var preKEntity: IKEntity? = null
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
                preNotEmptyEntityCount = 0
                preKEntity = null
                periodList.forEachIndexed { periodListIdx, _ ->
                    preAvgRiseList[periodListIdx] = 0f
                    preAvgDownList[periodListIdx] = 0f
                    result[periodListIdx][kEntityIdx] = null
                }
                return@forEachIndexed
            }

            periodList.forEachIndexed { periodListIdx, period ->
                val changeAmount =
                    if (kEntityIdx == 0) 0f else kEntity.getClosePrice() - (preKEntity?.getClosePrice()
                        ?: 0f)
                val n = min(preNotEmptyEntityCount + 1, period)
                val preAvgRise = if (kEntityIdx == 0) 0f else preAvgRiseList[periodListIdx]
                val preAvgDown = if (kEntityIdx == 0) 0f else preAvgDownList[periodListIdx]
                val avgRise =
                    ((if (n == 1) 0f else (preAvgRise * (n - 1))) + max(changeAmount, 0f)) / n
                val avgDown =
                    ((if (n == 1) 0f else (preAvgDown * (n - 1))) + max(-changeAmount, 0f)) / n
                result[periodListIdx][kEntityIdx] =
                    if (kEntityIdx == 0 || avgRise + avgDown == 0f) 0f else 100 * avgRise / (avgRise + avgDown)
                preAvgRiseList[periodListIdx] = avgRise
                preAvgDownList[periodListIdx] = avgDown
            }
            preKEntity = kEntity
            preNotEmptyEntityCount++
        }
        return result
    }
}