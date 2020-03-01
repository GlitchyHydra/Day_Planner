package com.example.weekplanner.activities

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weekplanner.MyValueFormatter
import com.example.weekplanner.R
import com.example.weekplanner.adapters.createArrayAdapter
import com.example.weekplanner.data.Statistic
import com.example.weekplanner.views.StatViewModel
import com.example.weekplanner.views.StatViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.Month
import java.util.Date
import java.util.Calendar

class StatActivity : AppCompatActivity() {

    private var pieChart: PieChart? = null
    private var barChart: BarChart? = null
    private var spinner: Spinner? = null
    private var allStats: List<Statistic> = mutableListOf()
    var statViewModel: StatViewModel? = null

    private val choosedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stats_layout)

        statViewModel = ViewModelProvider(this,
            StatViewModelFactory(application)
        ).get(StatViewModel::class.java)
        statViewModel!!.getAllStats().observe(this, Observer<List<Statistic>> {
            //Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            setPieData(it)
            setBarChartData(createValues(it))
        })

        //spinner = findViewById(R.id.statSpinner)
        //createArrayAdapter(this, R.array.date_range_array, spinner)
        //spinner!!.onItemSelectedListener = this

        ///val datePickerText = findViewById<TextView>(R.id.statDatePicker)
        /*datePickerText.setOnClickListener {

            val currentDay = choosedDate.get(Calendar.DAY_OF_MONTH)
            val currentMonth = choosedDate.get(Calendar.MONTH)
            val currentYear = choosedDate.get(Calendar.YEAR)

            val datePickerDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    datePickerText.text =
                        String.format("%02d %s %4d", day, Month.values()[month], year)
                    choosedDate.set(year, month, day)
                }, currentYear, currentMonth, currentDay)

            datePickerDialog.show()
        }*/

        pieChart = findViewById(R.id.pieChart)
        setupPieChart()
        setPieData(emptyList())
        barChart = findViewById(R.id.barChart)
        setupBarChart()
        //chooseItem(0)
    }

    private fun setupBarChart() {
        barChart?.axisLeft?.setDrawLabels(false)
        barChart?.axisRight?.setDrawLabels(false)
        barChart?.axisLeft?.setDrawGridLines(false)
        barChart?.xAxis?.setDrawGridLines(false)
        barChart?.description?.isEnabled = false
    }

    private fun Calendar.getDay(dateInLong: Long): Int {
        val date = Date(dateInLong)
        time = date
        return get(Calendar.DAY_OF_YEAR)
    }

    private fun Calendar.getYear(dateInLong: Long): Int {
        val date = Date(dateInLong)
        time = date
        return get(Calendar.YEAR)
    }

    /*private fun createYearValues(listOfStats: List<Statistic>) : List<CountOfTasks> {
        val calendar = Calendar.getInstance()
        val totalList = listOfStats.sortedBy { it.date!! }

        val completedPlans = totalList.filter { it.isSolved }
        val failedPlans = totalList.filter { !it.isSolved }

        val completedCount = completedPlans.groupingBy { calendar.getMonth(Date(it.date!!)) }
            .eachCount()
            .filter { it.value >= 1 }
        val failedCount = failedPlans.groupingBy { calendar.getMonth(Date(it.date!!)) }
            .eachCount()
            .filter { it.value >= 1 }

        Log.e("completedCounts", completedCount.toString())

        val listOfCounts = mutableListOf<CountOfTasks>()
        for (i in 1..12) {
            listOfCounts.add(CountOfTasks(failedCount[i] ?: 0,
                completedCount[i] ?: 0, i))
        }

        return listOfCounts
    }
*/

    data class BarDataCustom (
        val year: Int,
        val dayOfYear: Int,
        val isSolved: Boolean
    ) {
        val sumOfYearAndDay get() = year + dayOfYear
    }

    fun createCustomBarData(calendar: Calendar ,date: Long, isSolved: Boolean): BarDataCustom {
        val year = calendar.getYear(date)
        val day = calendar.getDay(date)
        return BarDataCustom(year, day, isSolved)
    }

    private fun createValues(listOfStats: List<Statistic>) : List<BarEntry> {
        val calendar = Calendar.getInstance()
        val totalList = listOfStats.sortedBy { it.date!! }
            .groupingBy { createCustomBarData(calendar, it.date!!, it.isSolved) }.eachCount()
            .filter { it.value >=1 }

        /*val setOfDate = listOfStats
            .groupingBy { calendar.getDay(it.date!!) + calendar.getYear(it.date)}.eachCount()
            .filter { it.value >=1 }
            .keys*/

        val completedPlans = totalList.filter { it.key.isSolved }
            .keys.groupingBy { it.sumOfYearAndDay }.eachCount().filter { it.value >=1 }
        val failedPlans = totalList.filter { !it.key.isSolved }
            .keys.groupingBy { it.sumOfYearAndDay }.eachCount().filter { it.value >=1 }

        val size = maxOf(completedPlans.size, failedPlans.size)

        val listOfCounts = mutableListOf<BarEntry>()
        Log.e("kortte", totalList.toString())
        for (i in totalList) {
            listOfCounts.add(BarEntry(i.key.sumOfYearAndDay.toFloat(),
                listOf(completedPlans[i.key.sumOfYearAndDay]?.toFloat() ?: 0f,
                failedPlans[i.key.sumOfYearAndDay]?.toFloat() ?: 0f).toFloatArray()))
        }
        Log.e("kortte", listOfCounts.toString())
        return listOfCounts
    }

    private fun setBarChartData(listOfStats: List<BarEntry>) {


        barChart?.setVisibleXRangeMaximum(5f)

        if (listOfStats.lastIndex > 5)
            barChart?.moveViewToX(listOfStats.lastIndex.toFloat())

        val barDataSet = BarDataSet(listOfStats, "tasks")
        barDataSet.setDrawIcons(false)
        barDataSet.stackLabels = listOf("completed", "failed").toTypedArray()
        barDataSet.colors = listOf(ColorTemplate.JOYFUL_COLORS[0], ColorTemplate.JOYFUL_COLORS[1])
        barDataSet.setDrawValues(false)

        val barData = BarData(barDataSet)
        barData.setValueFormatter(MyValueFormatter())
        barData.barWidth = 0.3f

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
        chart.description.isEnabled = false
        chart.isRotationEnabled = false
    }

    private fun setPieData(listOfStats: List<Statistic>) {
        val totalCount = listOfStats.size
        val counts = listOf(listOfStats.filter { !it.isSolved }.size.toFloat(),
            listOfStats.filter { it.isSolved }.size.toFloat())
        val completedEntry = PieEntry(counts[1], "completed")
        val failedEntry = PieEntry(counts[0], "failed")
        val values = listOf(failedEntry, completedEntry)

        val dataSet = PieDataSet(values, "tasks")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
        dataSet.setDrawValues(false)

        val data = PieData(dataSet)
        data.setValueTextSize(10f)
        data.setValueTextColor(Color.YELLOW)

        pieChart?.data = data

        pieChart?.centerText = completedEntry.toCenterText() + failedEntry.toCenterText()
    }

    private fun PieEntry.toCenterText() = label + "\n" + value.toInt() +
            " tasks" + "\n"

    /* override fun onNothingSelected(parent: AdapterView<*>?) {
     }

     override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
             = chooseItem(position)

     private fun getYear(date: Long) : Int {
         val calendar = Calendar.getInstance()
         calendar.timeInMillis = date
         return calendar.get(Calendar.YEAR)
     }

     private fun getMonth(date: Long) : Int {
         val calendar = Calendar.getInstance()
         calendar.timeInMillis = date
         return calendar.get(Calendar.DAY_OF_MONTH)
     }

     private fun getWeekOfMonth(date: Long) : Int {
         val calendar = Calendar.getInstance()
         calendar.timeInMillis = date
         return calendar.get(Calendar.WEEK_OF_YEAR)
     }

     private fun getDayOfYear(date: Long) : Int {
         val calendar = Calendar.getInstance()
         calendar.timeInMillis = date
         return calendar.get(Calendar.DAY_OF_YEAR)
     }

     private fun chooseItem(position: Int) {
         val calendar = Calendar.getInstance()
         when(position) {
             0 -> {
                 val list = createDayValues(allStats.filter {
                     getDayOfYear(it.date!!) == choosedDate.get(Calendar.DAY_OF_YEAR)
                 })
                 setBarChartData(list)
             }
             1 -> {
                 val list = createWeekValues(allStats
                     .filter {
                         getWeekOfMonth(it.date!!) == choosedDate.get(Calendar.WEEK_OF_YEAR)
                     })
                 setBarChartData(list)
             }
             2 -> {
                 val list = createMonthValues(allStats.filter {
                     getMonth(it.date!!) == choosedDate.get(Calendar.MONTH)
                 })
                 setBarChartData(list)
             }
             3 -> {
                 val list = createYearValues(allStats
                     .filter { getYear(it.date!!) == choosedDate.get(Calendar.YEAR) })
                 setBarChartData(list)
             }
         }
     }*/
}
