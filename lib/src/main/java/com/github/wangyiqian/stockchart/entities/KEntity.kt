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
    private val time: Long
) : IKEntity {

    override fun getHighPrice() = highPrice

    override fun getLowPrice() = lowPrice

    override fun getOpenPrice() = openPrice

    override fun getClosePrice() = closePrice

    override fun getVolume() = volume

    override fun getTime() = time
}