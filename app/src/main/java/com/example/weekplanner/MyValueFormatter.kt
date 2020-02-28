package com.example.weekplanner

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class MyValueFormatter : ValueFormatter() {

    private var mFormat: DecimalFormat? = null

    init {
        mFormat = DecimalFormat("######.0")
    }

    override fun getFormattedValue(value: Float): String {
        return mFormat?.format(value) + "$"
    }
}