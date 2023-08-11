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
 * 指数移动平均值（Exponential Moving Average）
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
object EMACalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val emaPeriodList = try {
            paramList.map { it.toInt() }
        } catch (tr: Throwable) {
            emptyList<Int>()
        }

        val result = MutableList(emaPeriodList.size) { MutableList<Float?>(input.size) { 0f } }
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
                emaPeriodList.forEachIndexed { periodListIdx, _ ->
                    result[periodListIdx][kEntityIdx] = null
                }
                return@forEachIndexed
            }
            emaPeriodList.forEachIndexed { periodListIdx, n ->
                if (kEntityIdx == 0 || input[kEntityIdx - 1].containFlag(FLAG_EMPTY)) {
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