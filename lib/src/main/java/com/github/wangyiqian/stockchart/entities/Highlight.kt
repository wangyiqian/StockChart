package com.github.wangyiqian.stockchart.entities

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/6
 */
class Highlight(
    /**
     * 实际坐标x
     */
    var x: Float = 0f,

    /**
     * 实际坐标y
     */
    var y: Float = 0f,

    /**
     * 逻辑坐标x
     */
    var valueX: Float = 0f,

    /**
     * 逻辑坐标y
     */
    var valueY: Float = 0f
) {

    /**
     * 获取对应K线数据下标
     */
    fun getIdx() = (valueX).toInt()

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Highlight) return false
        return x == other.x && y == other.y && valueX == other.valueX && valueY == other.valueY
    }


}