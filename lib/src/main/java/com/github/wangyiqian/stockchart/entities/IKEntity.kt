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
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
interface IKEntity {
    fun getHighPrice(): Float
    fun setHighPrice(price: Float)
    fun getLowPrice(): Float
    fun setLowPrice(price: Float)
    fun getOpenPrice(): Float
    fun setOpenPrice(price: Float)
    fun getClosePrice(): Float
    fun setClosePrice(price: Float)
    fun getVolume(): Long
    fun setVolume(volume: Long)
    fun getTime(): Long
    fun setTime(time: Long)

    /**
     * 分时均线价格
     */
    fun getAvgPrice(): Float?
    fun setAvgPrice(price: Float?)

    /**
     * 用于设置标记位 如[FLAG_EMPTY]、[FLAG_LINE_STARTER]
     */
    fun setFlag(flag: Int)

    /**
     * 标记位
     */
    fun getFlag(): Int
}