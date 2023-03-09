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

package com.github.wangyiqian.stockchart.index

import com.github.wangyiqian.stockchart.*
import com.github.wangyiqian.stockchart.entities.IKEntity

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/2/18
 */
open abstract class Index(
    var param: String,
    var startText: String,
    var startTextColor: Int,
    var textFormatter: (idx: Int, value: Float?) -> String,
    var textMarginLeft: Float,
    var textMarginTop: Float,
    var textSpace: Float,
    var textSize: Float
) {

    abstract fun calculate(input: List<IKEntity>): List<List<Float?>>

    class MA(
        param: String = DefaultIndexParams.MA,
        startText: String = DefaultIndexStartText.MA,
        startTextColor: Int = DEFAULT_INDEX_START_TEXT_COLOR,
        textFormatter: (idx: Int, value: Float?) -> String = DefaultIndexTextFormatter.MA,
        textMarginLeft: Float = DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTop,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = MACalculator.calculate(param, input)
    }

    class EMA(
        param: String = DefaultIndexParams.EMA,
        startText: String = DefaultIndexStartText.EMA,
        startTextColor: Int = DEFAULT_INDEX_START_TEXT_COLOR,
        textFormatter: (idx: Int, value: Float?) -> String = DefaultIndexTextFormatter.EMA,
        textMarginLeft: Float = DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor, textFormatter, textMarginLeft, textMarginTop, textSpace, textSize
    ) {
        override fun calculate(input: List<IKEntity>) = EMACalculator.calculate(param, input)
    }

    class BOLL(
        param: String = DefaultIndexParams.BOLL,
        startText: String = DefaultIndexStartText.BOLL,
        startTextColor: Int = DEFAULT_INDEX_START_TEXT_COLOR,
        textFormatter: (idx: Int, value: Float?) -> String = DefaultIndexTextFormatter.BOLL,
        textMarginLeft: Float = DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTopDp: Float = DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTopDp,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = BollCalculator.calculate(param, input)
    }

    class MACD(
        param: String = DefaultIndexParams.MACD,
        startText: String = DefaultIndexStartText.MACD,
        startTextColor: Int = DEFAULT_INDEX_START_TEXT_COLOR,
        textFormatter: (idx: Int, value: Float?) -> String = DefaultIndexTextFormatter.MACD,
        textMarginLeft: Float = DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTop,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = MACDCalculator.calculate(param, input)
    }

    class KDJ(
        param: String = DefaultIndexParams.KDJ,
        startText: String = DefaultIndexStartText.KDJ,
        startTextColor: Int = DEFAULT_INDEX_START_TEXT_COLOR,
        textFormatter: (idx: Int, value: Float?) -> String = DefaultIndexTextFormatter.KDJ,
        textMarginLeft: Float = DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTop: Float = DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTop,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = KDJCalculator.calculate(param, input)
    }

    class RSI(
        param: String = DefaultIndexParams.RSI,
        startText: String = "RSI",
        startTextColor: Int = DEFAULT_INDEX_START_TEXT_COLOR,
        textFormatter: (idx: Int, value: Float?) -> String = DefaultIndexTextFormatter.RSI,
        textMarginLeft: Float = DEFAULT_INDEX_TEXT_MARGIN_LEFT,
        textMarginTopDp: Float = DEFAULT_INDEX_TEXT_MARGIN_TOP,
        textSpace: Float = DEFAULT_INDEX_TEXT_SPACE,
        textSize: Float = DEFAULT_INDEX_TEXT_SIZE
    ) : Index(
        param,
        startText,
        startTextColor,
        textFormatter,
        textMarginLeft,
        textMarginTopDp,
        textSpace,
        textSize
    ) {
        override fun calculate(input: List<IKEntity>) = RSICalculator.calculate(param, input)
    }
}

