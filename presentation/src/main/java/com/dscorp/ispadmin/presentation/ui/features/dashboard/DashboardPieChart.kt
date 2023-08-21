package com.dscorp.ispadmin.presentation.ui.features.dashboard

import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class DashboardPieChart(private val chart: PieChart, data: ArrayList<PieEntry>) {


    init {
        chart.setUsePercentValues(true)
        chart.centerText = "Resumen de recaudacion"
        chart.setDrawCenterText(true)
        chart.setDrawEntryLabels(false)
        chart.legend.isEnabled = true
        chart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
    }

    fun setData(entries: ArrayList<PieEntry>) {
        val dataSet = PieDataSet(entries, null)
        dataSet.sliceSpace = 1f
        val colors = java.util.ArrayList<Int>()

        //SKY BLUE
        colors.add(Color.rgb(3, 169, 244))
        //BLUE
        colors.add(Color.rgb(63, 81, 181))
        colors.add(Color.rgb(255, 87, 34))
        dataSet.colors = colors
        dataSet.selectionShift = 0f;
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE;
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(chart))
        data.setValueTextSize(11f)
        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
    }

}