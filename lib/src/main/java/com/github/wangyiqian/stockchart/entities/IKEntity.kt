package com.github.wangyiqian.stockchart.entities

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/1/28
 */
interface IKEntity {
    fun getHighPrice(): Float
    fun getLowPrice(): Float
    fun getOpenPrice(): Float
    fun getClosePrice(): Float
    fun getVolume(): Long
    fun getTime(): Long
}