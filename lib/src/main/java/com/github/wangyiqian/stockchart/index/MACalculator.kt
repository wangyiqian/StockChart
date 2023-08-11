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
 * 移动平均线（Moving Average）
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/14
 */
object MACalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val periodList = try {
            paramList.map { it.toInt() }
        } catch (tr: Throwable) {
            emptyList<Int>()
        }

        val result = MutableList(periodList.size) { MutableList<Float?>(input.size) { 0f } }
        val pFromList = MutableList(periodList.size) { 0 }
        val pEndList = MutableList(periodList.size) { 0 }
        val sumList = MutableList(periodList.size) { 0f }
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
                periodList.forEachIndexed { periodListIdx, _ ->
                    result[periodListIdx][kEntityIdx] = null
                    sumList[periodListIdx] = 0f
                }
                return@forEachIndexed
            }

            periodList.forEachIndexed { periodListIdx, period ->
                if (kEntityIdx == 0 || input[kEntityIdx - 1].containFlag(FLAG_EMPTY)) {
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