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
    private var highPrice: Float,
    private var lowPrice: Float,
    private var openPrice: Float,
    private var closePrice: Float,
    private var volume: Long,
    private var time: Long,
    private var avgPrice: Float? = null,
    private var flag: Int = FLAG_DEFAULT
) : IKEntity {

    override fun getHighPrice() = highPrice

    override fun setHighPrice(price: Float) {
        this.highPrice = price
    }

    override fun getLowPrice() = lowPrice

    override fun setLowPrice(price: Float) {
        this.lowPrice = price
    }

    override fun getOpenPrice() = openPrice

    override fun setOpenPrice(price: Float) {
        this.openPrice = price
    }

    override fun getClosePrice() = closePrice

    override fun setClosePrice(price: Float) {
        this.closePrice = price
    }

    override fun getVolume() = volume

    override fun setVolume(volume: Long) {
        this.volume = volume
    }

    override fun getTime() = time

    override fun setTime(time: Long) {
        this.time = time
    }

    override fun getAvgPrice() = avgPrice

    override fun setAvgPrice(price: Float?) {
        this.avgPrice = price
    }

    override fun setFlag(flag: Int) {
        this.flag = flag
    }

    override fun getFlag(): Int {
        return flag
    }

    companion object {
        fun obtainEmptyKEntity() = KEntity(0f, 0f, 0f, 0f, 0, 0, null, FLAG_EMPTY)
    }
}