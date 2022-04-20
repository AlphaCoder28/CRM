package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.common.AxisValueFormatter
import com.goldmedal.crm.data.db.entities.TicketHistoryData
import com.goldmedal.crm.databinding.ActivityTicketHistoryBinding
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toast
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList


class TicketHistoryActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {


    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()

    private lateinit var viewModel: TicketViewModel
    private lateinit var binding: ActivityTicketHistoryBinding

//    private val count = 2
private val months = arrayOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

    private val lowerCaseMonths: List<String> = months.map {
        it.toLowerCase(Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        binding = ActivityTicketHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        initChart()

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getTicketHistory(user.UserId)
            }
        })
    }

    private fun initChart() {
        binding.chart.description.isEnabled = false
        binding.chart.axisRight.isEnabled = false
        binding.chart.setBackgroundColor(Color.WHITE)
        binding.chart.setDrawGridBackground(false)
        binding.chart.setDrawBarShadow(false)
        binding.chart.setExtraOffsets(5f,5f,5f,15f)
        binding.chart.isHighlightFullBarEnabled = false


        // draw bars behind lines
        binding.chart.drawOrder = arrayOf(
            DrawOrder.BAR, DrawOrder.LINE
        )

        val l: Legend = binding.chart.legend
        l.isWordWrapEnabled = true
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)

        val leftAxis: YAxis = binding.chart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        val xAxis: XAxis = binding.chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = -45f
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(months)

    }

    // ============================================================================================
    //  Datasets
    // ============================================================================================
    private fun generateLineData(list: List<TicketHistoryData?>?): LineData? {
        val d = LineData()
        list?.let {
            val entries: ArrayList<Entry> = ArrayList()
            for (index in list.indices)
            {
                val monthIndex = lowerCaseMonths.indexOf(list[index]?.Month?.toLowerCase(Locale.getDefault()))
                entries.add(
                    Entry(
                        monthIndex.toFloat(), //+ 0.5f
                        list[index]?.ClosedTicket?.toFloat() ?: 0f
                    )
                )
            }
            val set = LineDataSet(entries, "Closed")
            set.valueFormatter = AxisValueFormatter()
            set.color = Color.rgb(220, 60, 46)
            set.lineWidth = 2.5f
            set.setCircleColor(Color.rgb(60, 70, 85))
            set.circleRadius = 5f
            set.fillColor = Color.rgb(255, 193, 88)
            set.mode = LineDataSet.Mode.CUBIC_BEZIER
            set.setDrawValues(true)
            set.valueTextSize = 10f
            set.valueTextColor = Color.rgb(60, 70, 85)
            set.axisDependency = YAxis.AxisDependency.LEFT
            d.addDataSet(set)
        }
        return d
    }

    private fun generateBarData(list: List<TicketHistoryData?>?): BarData? {
        val entries: ArrayList<BarEntry> = ArrayList()
        list?.let {
            for (index in list.indices) {
                val monthIndex = lowerCaseMonths.indexOf(list[index]?.Month?.toLowerCase(Locale.getDefault()))
                entries.add(
                    BarEntry(
                        monthIndex.toFloat(),
                        list[index]?.TotalTicket?.toFloat() ?: 0f
                    )
                )
            }
        }
        val set = BarDataSet(entries, "Total")
        set.color = Color.rgb(255, 193, 88)
        set.valueTextColor = Color.rgb(131, 154, 170)
        set.valueTextSize = 10f
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueFormatter = AxisValueFormatter()

        return BarData(set)
    }

    private fun updateChart(list: List<TicketHistoryData?>){
        val data = CombinedData()

        //var data2 = list as MutableList<TicketHistoryData>
//
//        data2.add(TicketHistoryData(12, "MAR", 31))
//            data2.add(TicketHistoryData(11, "Apr", 20))
//            data2.add(TicketHistoryData(17, "MAY", 24))
//            data2.add(TicketHistoryData(3, "JUN", 5))
//            data2.add(TicketHistoryData(4, "jul", 7))
//            data2.add(TicketHistoryData(11, "aug", 14))
//            data2.add(TicketHistoryData(0, "sep", 1))
//            data2.add(TicketHistoryData(0, "oct", 5))
//            data2.add(TicketHistoryData(1, "nov", 4))
//            data2.add(TicketHistoryData(3, "Dec", 3))

//        data.setData(generateLineData(data2))
            data.setData(generateLineData(list))
            data.setData(generateBarData(list))
//        data.setData(generateBarData(data2))

//        data.setValueTypeface(tfLight)
        binding.chart.xAxis.labelCount = data.entryCount
        binding.chart.xAxis.axisMinimum = data.xMin - 0.5f
        binding.chart.xAxis.axisMaximum = data.xMax + 0.5f
        binding.chart.animateY(1000)
        binding.chart.data = data
        binding.chart.invalidate()
    }

    override fun onStarted(callFrom: String) {
        //TODO
    }



    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        if (callFrom == "ticket_history") {
            //update chart
         updateChart(_object as List<TicketHistoryData?>)
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (callFrom == "ticket_history") {

//            birthday_progress_bar?.stop()

            viewModel.getTicketHistoryDetail().observe(this, Observer { data ->
                print("outside - - - " + data.size)
                if (data == null) {
                    if (isNetworkError) {
                       binding.rootLayout.snackbar("No internet Connection!")
                    } else {
                        //No data found
                    }
                } else {
                    val historyData = data as List<TicketHistoryData?>

                    //update chart
                    updateChart(historyData)

                }
            })
        }
    }

    override fun onValidationError(message: String, callFrom: String) {
      binding.rootLayout.snackbar(message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TicketHistoryActivity::class.java))
        }
    }
}