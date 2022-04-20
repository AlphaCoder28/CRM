package com.goldmedal.crm.ui.dashboard.Manager

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.goldmedal.crm.R
import com.goldmedal.crm.common.AxisValueFormatter
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.data.db.entities.TicketHistoryData
import com.goldmedal.crm.data.model.*
import com.goldmedal.crm.databinding.FragmentManagerBinding
import com.goldmedal.crm.ui.auth.AuthListener
import com.goldmedal.crm.ui.dashboard.home.HomeViewModel
import com.goldmedal.crm.ui.dashboard.home.HomeViewModelFactory
import com.goldmedal.crm.util.*
import kotlinx.android.synthetic.main.fragment_manager.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

import com.github.mikephil.charting.data.*


class ManagerHomeFragment : Fragment(), KodeinAware, DashboardApiListener<Any> {

    var mContext: Context? = null
    override val kodein by kodein()

    private val factory: HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel

    private lateinit var managerFragmentBinding: FragmentManagerBinding

    private val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    var days = arrayOf("Sunday", "Monday", "Tuesday", "Thursday", "Friday", "Saturday")

    private val lowerCaseMonths: List<String> = months.map {
        it.toLowerCase(Locale.getDefault())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        managerFragmentBinding =
            DataBindingUtil.inflate(inflater, com.goldmedal.crm.R.layout.fragment_manager, container, false)
        return managerFragmentBinding.root
        // return inflater.inflate(R.layout.fragment_manager, container, false)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance() =
//            ManagerFragment().apply {
//                arguments = Bundle().apply {
//
//                }
//            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        managerFragmentBinding.viewmodelHome = viewModel

        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it != null) {

                viewModel.getManagerTicketData(it.UserId ?: 0, it.ServiceCenterID ?: 0)
                viewModel.getManagerMonthwise(it.UserId ?: 0, it.ServiceCenterID ?: 0)

            }

        })

    }




    override fun onStarted(callFrom: String) {

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String, timestamp: String) {


        if (callFrom.equals("manager_ticket_monthwise_count")) {

            val data = _object as List<MonthwiseData?>

            data?.let { updateAnalysisChart(it)
                analysisChart()}

        }

        if (callFrom == "manager_ticket_count") {

            val data = _object as List<Data?>
            bindTicketUI(data)

        }

    }

    private fun updateAnalysisChart(it: List<MonthwiseData?>) {

        rlAnalysisChart.removeAllViews()
        val managerAnalysisChart = ManagerAnalysisChart(context,it)
        rlAnalysisChart.addView(managerAnalysisChart)

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

    }

    override fun onValidationError(message: String, callFrom: String) {

    }


    private fun bindTicketUI(list: List<Data?>?) = Coroutines.main {
        list?.let {
            tv_Open_ticket?.text = it[0]?.OpenTickets.toString()
            tv_Assign_ticket?.text = it[0]?.AssignedTickets.toString()
            tv_Reassign_ticket?.text = it[0]?.ReassignTickets.toString()
            tv_processed_ticket?.text = it[0]?.ProcessedTickets.toString()
            tv_Closed_ticket?.text = it[0]?.ClosedTickets.toString()
            tv_Pending_Assign?.text = it[0]?.PendingTickets.toString()
            tv_Rejected_ticket?.text = it[0]?.RejectedTicketsse.toString()
        }
    }


    private fun analysisChart(){

        // creating a new bar data set.
        // creating a new bar data set.
       val barDataSet1 = BarDataSet(getBarEntriesOne(), "First Set")
//        barDataSet1.setColor(Color.rgb(220, 60, 46))
        context?.resources?.let { barDataSet1.setColor(it?.getColor(R.color.bar_blue)) }

        val barDataSet2 = BarDataSet(getBarEntriesTwo(), "Second Set")
//        barDataSet2.setColor(Color.rgb(60, 70, 85))
        context?.resources?.getColor(R.color.bar_red)?.let { barDataSet2.setColor(it) }

        // below line is to add bar data set to our bar data.

        // below line is to add bar data set to our bar data.
        val data = BarData(barDataSet1, barDataSet2)

        // after adding data to our bar data we
        // are setting that data to our bar chart.

        // after adding data to our bar data we
        // are setting that data to our bar chart.
        idBarChart.setData(data)

        // below line is to remove description
        // label of our bar chart.

        // below line is to remove description
        // label of our bar chart.
        idBarChart.getDescription().setEnabled(false)

        // below line is to get x axis
        // of our bar chart.

        // below line is to get x axis
        // of our bar chart.
        val xAxis: XAxis = idBarChart.getXAxis()

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.

        // below line is to set value formatter to our x-axis and
        // we are adding our days to our x axis.
        xAxis.valueFormatter = IndexAxisValueFormatter(days)

        // below line is to set center axis
        // labels to our bar chart.

        // below line is to set center axis
        // labels to our bar chart.
        xAxis.setCenterAxisLabels(true)

        // below line is to set position
        // to our x-axis to bottom.

        // below line is to set position
        // to our x-axis to bottom.
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // below line is to set granularity
        // to our x axis labels.

        // below line is to set granularity
        // to our x axis labels.
        xAxis.granularity = 1f

        // below line is to enable
        // granularity to our x axis.

        // below line is to enable
        // granularity to our x axis.
        xAxis.isGranularityEnabled = true

        // below line is to make our
        // bar chart as draggable.

        // below line is to make our
        // bar chart as draggable.
        idBarChart.setDragEnabled(true)

        // below line is to make visible
        // range for our bar chart.

        // below line is to make visible
        // range for our bar chart.
        idBarChart.setVisibleXRangeMaximum(3f)

        // below line is to add bar
        // space to our chart.

        // below line is to add bar
        // space to our chart.
        val barSpace = 0.0f

        // below line is use to add group
        // spacing to our bar chart.

        // below line is use to add group
        // spacing to our bar chart.
        val groupSpace = 0.5f

        // we are setting width of
        // bar in below line.

        // we are setting width of
        // bar in below line.
        data.barWidth = 0.25f

        // below line is to set minimum
        // axis to our chart.

        // below line is to set minimum
        // axis to our chart.
        idBarChart.getXAxis().setAxisMinimum(0f)

        // below line is to
        // animate our chart.

        // below line is to
        // animate our chart.
        idBarChart.animate()

        // below line is to group bars
        // and add spacing to it.

        // below line is to group bars
        // and add spacing to it.
        idBarChart.groupBars(0f, groupSpace, barSpace)

        // below line is to invalidate
        // our bar chart.

        // below line is to invalidate
        // our bar chart.
        idBarChart.invalidate()



//        val xAxis: XAxis = idBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = -45f
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(months)

        val leftAxis: YAxis = idBarChart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

    }

    // array list for first set
    private fun getBarEntriesOne(): ArrayList<BarEntry> {

        // creating a new array list
       val barEntries : ArrayList<BarEntry> = ArrayList()


        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(BarEntry(1f, 4f))
        barEntries.add(BarEntry(2f, 6f))
        barEntries.add(BarEntry(3f, 8f))
        barEntries.add(BarEntry(4f, 2f))
        barEntries.add(BarEntry(5f, 4f))
        barEntries.add(BarEntry(6f, 1f))
        return barEntries
    }

    // array list for second set.
    private fun getBarEntriesTwo(): ArrayList<BarEntry> {

        // creating a new array list
      val barEntries : ArrayList<BarEntry> = ArrayList()

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntries.add(BarEntry(1f, 8f))
        barEntries.add(BarEntry(2f, 12f))
        barEntries.add(BarEntry(3f, 4f))
        barEntries.add(BarEntry(4f, 1f))
        barEntries.add(BarEntry(5f, 7f))
        barEntries.add(BarEntry(6f, 3f))
        return barEntries
    }

//    private fun initChart() {
//        ManagerAnalysisChart.description.isEnabled = false
//        ManagerAnalysisChart.axisRight.isEnabled = false
//        ManagerAnalysisChart.setBackgroundColor(Color.WHITE)
//        ManagerAnalysisChart.setDrawGridBackground(false)
//        ManagerAnalysisChart.setDrawBarShadow(false)
//        ManagerAnalysisChart.setExtraOffsets(5f,5f,5f,15f)
//        ManagerAnalysisChart.isHighlightFullBarEnabled = false
//
//
//        // draw bars behind lines
////        ManagerAnalysisChart.drawOrder = arrayOf(
////            CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
////        )
//
//        val l: Legend = ManagerAnalysisChart.legend
//        l.isWordWrapEnabled = true
//        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(false)
//
//        val leftAxis: YAxis = ManagerAnalysisChart.axisLeft
//        leftAxis.setDrawGridLines(true)
//        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
//
//
//        val xAxis: XAxis = ManagerAnalysisChart.xAxis
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.labelRotationAngle = -45f
//        xAxis.setDrawGridLines(false)
//        xAxis.granularity = 1f
//        xAxis.valueFormatter = IndexAxisValueFormatter(months)
//
//    }
//
//        private fun updateChart(list: List<MonthwiseData?>){
//           val data = CombinedData()
//
//           data.setData(generateBarData(list))
//
//           ManagerAnalysisChart.xAxis.labelCount = data.entryCount
//           ManagerAnalysisChart.xAxis.axisMinimum = data.xMin - 0.5f
//           ManagerAnalysisChart.xAxis.axisMaximum = data.xMax + 0.5f
//           ManagerAnalysisChart.animateY(1000)
//           ManagerAnalysisChart.data = data
//           ManagerAnalysisChart.invalidate()
//       }
//
//      private fun generateBarData(list: List<MonthwiseData?>?): BarData? {
//
//           val entries: ArrayList<BarEntry> = ArrayList()
//           val entries1: ArrayList<BarEntry> = ArrayList()
//
//           list?.let {
//
//               val xVals: ArrayList<BarEntry> = ArrayList()
//
//               val yVals1: ArrayList<BarEntry> = ArrayList()
//               val yVals2: ArrayList<BarEntry> = ArrayList()
//
//               for (index in list.indices) {
//                   val monthIndex = lowerCaseMonths.indexOf(list[index]?.MonthName?.toLowerCase(Locale.getDefault()))
//
//                   entries.add(
//                       BarEntry(
//                           monthIndex.toFloat(),
//                           list[index]?.Opentk?.toFloat() ?: 0f
//                       )
//                   )
//
//                   entries1.add(
//                       BarEntry(
//                           monthIndex.toFloat(),
//                           list[index]?.Closetk?.toFloat() ?: 0f
//                       )
//                   )
//               }
//           }
//
//           val set = BarDataSet(entries, "Open")
//           set.color = Color.rgb(255, 93, 93)
//           set.valueTextColor = Color.rgb(131, 154, 170)
//           set.valueTextSize = 10f
//           set.axisDependency = YAxis.AxisDependency.LEFT
//           set.valueFormatter = AxisValueFormatter()
//
//           val set1 = BarDataSet(entries1, "Close")
//           set.color = Color.rgb(37, 169, 225)
//           set.valueTextColor = Color.rgb(131, 154, 170)
//           set.valueTextSize = 10f
//           set.axisDependency = YAxis.AxisDependency.LEFT
//           set.valueFormatter = AxisValueFormatter()
//
//           return BarData(set,set1)
//       }
}