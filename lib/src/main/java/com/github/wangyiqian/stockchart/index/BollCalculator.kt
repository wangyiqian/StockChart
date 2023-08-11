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
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * BOLL 布林线
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/16
 */
object BollCalculator : ICalculator {

    override fun calculate(param: String, input: List<IKEntity>): List<List<Float?>> {
        val paramList = param.split(",")
        val n = try {
            paramList[0].toInt()
        } catch (tr: Throwable) {
            null
        }
        val k = try {
            paramList[1].toInt()
        } catch (tr: Throwable) {
            null
        }

        if (n == null || k == null) {
            return emptyList()
        }

        // 1. MB 2.UP 3.DN
        val result = MutableList(3) { MutableList<Float?>(input.size) { 0f } }
        val mbIdx = 0
        val upIdx = 1
        val dnIdx = 2

        var pFrom = 0
        var pEnd = 0
        var sum = 0f
        input.forEachIndexed { kEntityIdx, kEntity ->
            if (kEntity.containFlag(FLAG_EMPTY)) {
                result[mbIdx][kEntityIdx] = null
                result[upIdx][kEntityIdx] = null
                result[dnIdx][kEntityIdx] = null
                sum = 0f
                return@forEachIndexed
            }

            if (kEntityIdx == 0 || input[kEntityIdx - 1].containFlag(FLAG_EMPTY)) {
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