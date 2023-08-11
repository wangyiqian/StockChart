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

package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.entities.FLAG_EMPTY
import com.github.wangyiqian.stockchart.entities.IKEntity
import com.github.wangyiqian.stockchart.entities.containFlag
import kotlin.math.max
import kotlin.math.min

/**
 * KDJ 随机指标
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
object KDJCalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val n = try {
            paramList[0].toInt()
        } catch (tr: Throwable) {
            null
        }
        val kn = try {
            paramList[1].toInt()
        } catch (tr: Throwable) {
            null
        }
        val dn = try {
            paramList[2].toInt()
        } catch (tr: Throwable) {
            null
        }

        if (n == null || kn == null || dn == null) {
            return emptyList()
        }

        val result = MutableList(3) { MutableList<Float?>(input.size) { 0f } }
        val kIdx = 0
        val dIdx = 1
        val jIdx = 2
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
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