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

package com.github.wangyiqian.stockchart.listener

import com.github.wangyiqian.stockchart.entities.GestureEvent

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/11/29
 */
interface OnGestureListener {

    /**
     * 滑动中
     */
    fun onHScrolling() {}

    /**
     * 开始fling
     */
    fun onFlingBegin() {}

    /**
     * 手指离开屏幕
     */
    fun onTouchLeave() {}

    /**
     * 开始缩放
     */
    fun onScaleBegin(focusX: Float) {}

    /**
     * 缩放中
     */
    fun onScaling(totalScaleX: Float) {}

    /**
     * 单击
     */
    fun onTap(x: Float, y: Float) {}

    /**
     * 开始长按
     */
    fun onLongPressBegin(x: Float, y: Float) {}

    /**
     * 长按中
     */
    fun onLongPressing(x: Float, y: Float) {}

    /**
     * 结束长按
     */
    fun onLongPressEnd(x: Float, y: Float) {}
}