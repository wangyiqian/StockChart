package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.EmptyKEntity
import com.github.wangyiqian.stockchart.entities.IKEntity
import kotlin.math.max
import kotlin.math.min

/**
 * KDJ 随机指标
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
object KDJCalculator: ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val n = paramList[0].toInt()
        val kn = paramList[1].toInt()
        val dn = paramList[2].toInt()

        val result = MutableList(3) { MutableList<Float?>(input.size) { 0f } }
        val kIdx = 0
        val dIdx = 1
        val jIdx = 2
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity is EmptyKEntity) {
                result[kIdx][kEntityIdx] = null
                result[dIdx][kEntityIdx] = null
                result[jIdx][kEntityIdx] = null
                return@forEachIndexed
            }
            val c = kEntity.getClosePrice()
            var l = kEntity.getLowPrice()
            var h = kEntity.getHighPrice()
            for (i in kEntityIdx - 1 downTo max(0, kEntityIdx - n + 1)) {
                l = min(l, input[i].getLowPrice())
                h = max(h, input[i].getHighPrice())
            }

            if (h == l) {
                result[kIdx][kEntityIdx] = null
                result[dIdx][kEntityIdx] = null
                result[jIdx][kEntityIdx] = null
                return@forEachIndexed
            }

            val rsv = (c - l) / (h - l)
            val preK =
                if (kEntityIdx == 0 || result[kIdx][kEntityIdx - 1] == null) 0f else result[kIdx][kEntityIdx]!!
            val k = (kn - 1f) / kn * preK + 1f / kn * rsv
            val preD =
                if (kEntityIdx == 0 || result[dIdx][kEntityIdx - 1] == null) 0f else result[dIdx][kEntityIdx]!!
            val d = (dn - 1f) / dn * preD + 1f / dn * k
            val j = 3f * k - 2f * d
            result[kIdx][kEntityIdx] = k
            result[dIdx][kEntityIdx] = d
            result[jIdx][kEntityIdx] = j
        }
        return result
    }
}