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

/**
 * 指数平滑异同移动平均线（Moving Average Convergence and Divergence）
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
object MACDCalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val shortPeriod = try {
            paramList[0].toInt()
        } catch (tr: Throwable) {
            null
        }
        val longPeriod = try {
            paramList[1].toInt()
        } catch (tr: Throwable) {
            null
        }
        val avgPeriod = try {
            paramList[2].toInt()
        } catch (tr: Throwable) {
            null
        }
        if (shortPeriod == null || longPeriod == null || avgPeriod == null) {
            return emptyList()
        }

        val result = MutableList(3) { MutableList<Float?>(input.size) { 0f } }
        val difIdx = 0
        val deaIdx = 1
        val macdIdx = 2

        var preEmaShort = 0f
        var preEmaLong = 0f
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
                result[difIdx][kEntityIdx] = null
                result[deaIdx][kEntityIdx] = null
                result[macdIdx][kEntityIdx] = null
                return@forEachIndexed
            }

            if (kEntityIdx == 0 || input[kEntityIdx - 1].containFlag(FLAG_EMPTY)) {
                result[difIdx][kEntityIdx] = 0f
                result[deaIdx][kEntityIdx] = 0f
                result[macdIdx][kEntityIdx] = 0f
                preEmaShort = kEntity.getClosePrice()
                preEmaLong = kEntity.getClosePrice()
            } else {
                val emaShort =
                    2f / (shortPeriod + 1) * kEntity.getClosePrice() + (shortPeriod - 1f) / (shortPeriod + 1f) * preEmaShort
                val emaLong =
                    2f / (longPeriod + 1) * kEntity.getClosePrice() + (longPeriod - 1f) / (longPeriod + 1f) * preEmaLong
                val dif = emaShort - emaLong
                val dea =
                    2f / (avgPeriod + 1) * dif + (avgPeriod - 1f) / (avgPeriod + 1f) * result[deaIdx][kEntityIdx - 1]!!
                val macd = (dif - dea) * 2

                result[difIdx][kEntityIdx] = dif
                result[deaIdx][kEntityIdx] = dea
                result[macdIdx][kEntityIdx] = macd

                preEmaShort = emaShort
                preEmaLong = emaLong
            }
        }
        return result
    }

}