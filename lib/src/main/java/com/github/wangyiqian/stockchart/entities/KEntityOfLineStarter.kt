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
 * 封装了一层KEntity，目的是为了实现五日线这种不同日的折线不需要相连，每日起始第一个点使用此类封装即可
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/22
 */
@Deprecated("")
open class KEntityOfLineStarter(private val kEntity: IKEntity) : IKEntity {

    override fun getHighPrice() = kEntity.getHighPrice()

    override fun setHighPrice(price: Float) {
        kEntity.setHighPrice(price)
    }

    override fun getLowPrice() = kEntity.getLowPrice()

    override fun setLowPrice(price: Float) {
        kEntity.setLowPrice(price)
    }

    override fun getOpenPrice() = kEntity.getOpenPrice()

    override fun setOpenPrice(price: Float) {
        kEntity.setOpenPrice(price)
    }

    override fun getClosePrice() = kEntity.getClosePrice()

    override fun setClosePrice(price: Float) {
        kEntity.setClosePrice(price)
    }

    override fun getVolume() = kEntity.getVolume()

    override fun setVolume(volume: Long) {
        kEntity.setVolume(volume)
    }

    override fun getTime() = kEntity.getTime()

    override fun setTime(time: Long) {
        kEntity.setTime(time)
    }

    override fun getAvgPrice() = kEntity.getAvgPrice()

    override fun setAvgPrice(price: Float?) {
        kEntity.setAvgPrice(price)
    }

    override fun setFlag(flag: Int) {
        kEntity.setFlag(flag)
    }

    override fun getFlag() = FLAG_LINE_STARTER
}