package com.example.weekplanner.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weekplanner.MyValueFormatter
import com.example.weekplanner.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

class StatActivity : AppCompatActivity() {

    private var pieChart: PieChart? = null
    private var barChart: BarChart? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pie_fragment)
        pieChart = findViewById<PieChart>(R.id.pieChart)
        setupPieChart()
        setPieData()
        barChart = findViewById(R.id.barChart)
        setupBarChart()
        setBarChartData(10)
    }

    private fun setupBarChart() {
        barChart?.setMaxVisibleValueCount(40)

    }

    private fun setBarChartData(count: Int) {
        val yValues = mutableListOf<BarEntry>()
        for (i in 0..count) {
            val val1 = Math.random().toFloat() * count + 20f
            val val2 = Math.random().toFloat() * count + 20f

            yValues.add(BarEntry(i.toFloat(), listOf( val1, val2).toFloatArray()))
        }

        val barDataSet = BarDataSet(yValues, "Stat USA")
        barDataSet.setDrawIcons(false)
        barDataSet.stackLabels = listOf("completed", "failed").toTypedArray()
        barDataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()

        val barData = BarData(barDataSet)
        barData.setValueFormatter(MyValueFormatter())

        barChart?.data = barData
        barChart?.setFitBars(true)
        barChart?.invalidate()
    }


    private fun setupPieChart() {
        val chart = pieChart!!
        chart.setUsePercentValues(true)
        chart.description.isEnabled = true
        chart.setExtraOffsets(5f, 10f, 5f, 5f)
        chart.dragDecelerationFrictionCoef = 0.95f
        chart.isDrawHoleEnabled = true
        chart.setHoleColor(Color.WHITE)
        chart.transparentCircleRadius = 61f
        chart.holeRadius = 85f
        chart.setDrawEntryLabels(false)
        chart.setDrawCenterText(true)
    }

    private fun setPieData() {
        val counts = intent.getFloatArrayExtra("counts")
        val total = intent.getIntExtra("total", 200)
        val completedEntry = PieEntry(total.toFloat(), "completed")
        val failedEntry = PieEntry(55f, "failed")
        val values = listOf(failedEntry, completedEntry)

        val dataSet = PieDataSet(values, "tasks")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()

        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.YELLOW)

        pieChart?.data = data

        pieChart?.centerText = completedEntry.toCenterText() + failedEntry.toCenterText()
    }

    fun PieEntry.toCenterText() = label + "\n" + value.toInt() +
            " tasks" + "\n"
}