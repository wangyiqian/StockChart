package com.github.wangyiqian.stockchart.entities

/**
 * 封装了一层KEntity，目的是为了实现五日线这种不同日的折线不需要相连，每日起始第一个点使用此类封装即可
 *
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/22
 */
class KEntityOfLineStarter(val kEntity: IKEntity) : IKEntity {

    override fun getHighPrice() = kEntity.getHighPrice()

    override fun getLowPrice() = kEntity.getLowPrice()

    override fun getOpenPrice() = kEntity.getOpenPrice()

    override fun getClosePrice() = kEntity.getClosePrice()

    override fun getVolume() = kEntity.getVolume()

    override fun getTime() = kEntity.getTime()
}