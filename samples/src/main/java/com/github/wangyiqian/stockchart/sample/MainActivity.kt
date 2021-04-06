package com.github.wangyiqian.stockchart.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.github.wangyiqian.stockchart.sample.R.*
import com.github.wangyiqian.stockchart.sample.sample1.Sample1Activity
import com.github.wangyiqian.stockchart.sample.sample2.Sample2Activity

/**
 * @author wangyiqian E-mail: wangyiqian9891@gmail.com
 * @version 创建时间: 2021/4/4
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
    }

    fun sample1(v: View) {
        startActivity(Intent(this, Sample1Activity::class.java))
    }

    fun sample2(view: View) {
        startActivity(Intent(this, Sample2Activity::class.java))
    }
}