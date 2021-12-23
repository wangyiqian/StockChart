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

package com.github.wangyiqian.stockchart.sample.sample3.data

import com.github.wangyiqian.stockchart.entities.FLAG_DEFAULT
import com.github.wangyiqian.stockchart.entities.IKEntity

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/5/14
 */
class ActiveChartKEntity(
    private var price: Float,
    private var avgPrice: Float?,
    private var time: Long,
    private var volume: Long,
    private var active: ActiveInfo?,
    private var flag: Int = FLAG_DEFAULT
) : IKEntity, IActiveChartKEntity {
    override fun getAvgPrice() = avgPrice

    override fun setAvgPrice(price: Float?) {
        this.avgPrice = price
    }

    override fun getClosePrice() = price

    override fun setClosePrice(price: Float) {
        this.price = price
    }

    override fun getHighPrice() = price

    override fun setHighPrice(price: Float) {
        this.price = price
    }

    override fun getLowPrice() = price

    override fun setLowPrice(price: Float) {
        this.price = price
    }

    override fun getOpenPrice() = price

    override fun setOpenPrice(price: Float) {
        this.price = price
    }

    override fun getTime() = time

    override fun setTime(time: Long) {
        this.time = time
    }

    override fun getVolume() = volume

    override fun setVolume(volume: Long) {
        this.volume = volume
    }

    override fun getActiveInfo() = active

    override fun setFlag(flag: Int) {
        this.flag = flag
    }

    override fun getFlag() = flag
}