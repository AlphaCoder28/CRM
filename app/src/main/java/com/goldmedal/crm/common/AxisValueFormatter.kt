package com.goldmedal.crm.common

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter

class AxisValueFormatter : ValueFormatter() {
//    private val format = DecimalFormat("###,##0.0")
    // override this for e.g. LineChart or ScatterChart
    override fun getPointLabel(entry: Entry?): String {
        return entry?.y?.toInt().toString()
    }
    // override this for BarChart
    override fun getBarLabel(barEntry: BarEntry?): String {
//        return format.format(barEntry?.y)
        return (barEntry?.y)?.toInt().toString()
    }
    // override this for custom formatting of XAxis or YAxis labels
//    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
////        return format.format(value)
//        return value.toString()
//    }
    // ... override other methods for the other chart types
}
