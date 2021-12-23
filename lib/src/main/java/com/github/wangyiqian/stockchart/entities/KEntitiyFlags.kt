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
 * @version 创建时间: 2021/12/23
 */

const val FLAG_DEFAULT = 0

/**
 * 空点
 */
const val FLAG_EMPTY = 1 shl 0

/**
 * 折线起始点，目的是为了实现五日线这种不同日的折线不需要相连，每日起始第一个点使用此标记位即可
 */
const val FLAG_LINE_STARTER = 1 shl 1

fun IKEntity.containFlag(flag: Int) = getFlag() and flag == flag