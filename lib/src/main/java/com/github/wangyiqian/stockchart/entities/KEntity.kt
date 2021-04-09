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

package com.github.wangyiqian.stockchart.entities

/**
 * K线数据实体类
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
open class KEntity(
    private val highPrice: Float,
    private val lowPrice: Float,
    private val openPrice: Float,
    private val closePrice: Float,
    private val volume: Long,
    private val time: Long,
    private val avgPrice: Float? = null
) : IKEntity {

    override fun getHighPrice() = highPrice

    override fun getLowPrice() = lowPrice

    override fun getOpenPrice() = openPrice

    override fun getClosePrice() = closePrice

    override fun getVolume() = volume

    override fun getTime() = time

    override fun getAvgPrice() = avgPrice
}