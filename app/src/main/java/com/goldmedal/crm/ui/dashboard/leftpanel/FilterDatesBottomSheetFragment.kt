package com.goldmedal.crm.ui.dashboard.leftpanel

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.goldmedal.crm.R
import com.goldmedal.crm.util.toString
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class FilterDatesBottomSheetFragment : BottomSheetDialogFragment() {


    private var selectedTimeFilter = "last_3_months"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_filter_dates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews(view)
    }

    private fun setUpViews(view: View) {

        val radioGrp = view.findViewById<RadioGroup>(R.id.radio_group_filter)
        val radioButtonThreeMonths = view.findViewById<RadioButton>(R.id.radio_button_last_3_months)
        val radioButtonSixMonths = view.findViewById<RadioButton>(R.id.radio_button_last_6_months)
        val radioButtonCurrentYear = view.findViewById<RadioButton>(R.id.radio_button_current_year)
        val radioButtonLastYear = view.findViewById<RadioButton>(R.id.radio_button_last_year)
        val radioButtonPenultimateYear =
            view.findViewById<RadioButton>(R.id.radio_button_penultimate_year)


        val calendar = Calendar.getInstance()
        val today = calendar.time
        val currentYear = calendar[Calendar.YEAR]
        val lastYear = currentYear - 1
        val penultimateYear = lastYear - 1

        radioButtonCurrentYear.text = currentYear.toString()
        radioButtonLastYear.text = lastYear.toString()
        radioButtonPenultimateYear.text = penultimateYear.toString()


        when (selectedTimeFilter) {

            "last_3_months" -> radioButtonThreeMonths.isChecked = true
            "last_6_months" -> radioButtonSixMonths.isChecked = true
            "current_year" -> radioButtonCurrentYear.isChecked = true
            "last_year" -> radioButtonLastYear.isChecked = true
            "penultimate_year" -> radioButtonPenultimateYear.isChecked = true

        }


        radioGrp.setOnCheckedChangeListener { radioGroup, i ->

            when (radioGroup.checkedRadioButtonId) {

                R.id.radio_button_last_3_months -> {


                    val threeMonthsCalendar = Calendar.getInstance()
                    threeMonthsCalendar.add(Calendar.MONTH, -3)

                    val startDate = threeMonthsCalendar.time

//                    val selectedYear = threeMonthsCalendar[Calendar.YEAR]


//                    val last3Months = threeMonthsCalendar[Calendar.MONTH] + 1

                    formDates(
                        startDate = startDate,
                        endDate = today,
                        param = "last_3_months",
                        selectedText = radioButtonThreeMonths.text.toString()
                    )


                }
                R.id.radio_button_last_6_months -> {


                    val sixMonthsCalendar = Calendar.getInstance()
                    sixMonthsCalendar.add(Calendar.MONTH, -6)


                    val startDate = sixMonthsCalendar.time

//                    val last6Months = sixMonthsCalendar[Calendar.MONTH] + 1
//                    val selectedYear = sixMonthsCalendar[Calendar.YEAR]

                    formDates(
                        startDate = startDate,
                        endDate = today,
                        param = "last_6_months",
                        selectedText = radioButtonSixMonths.text.toString()
                    )
                }
                R.id.radio_button_current_year -> {


                    val currentYearCalendar = Calendar.getInstance()
                    currentYearCalendar[Calendar.MONTH] = Calendar.JANUARY
                    val startDate = currentYearCalendar.time



                    formDates(
                        startDate = startDate,
                        endDate = today,
                        param = "current_year",
                        selectedText = radioButtonCurrentYear.text.toString()
                    )


                }
                R.id.radio_button_last_year -> {



                    val lastYearCalendar = Calendar.getInstance()
                    lastYearCalendar.add(Calendar.YEAR, -1)
                    lastYearCalendar[Calendar.MONTH] = Calendar.JANUARY
                    val startDate = lastYearCalendar.time

                    lastYearCalendar[Calendar.MONTH] = Calendar.DECEMBER
                    val endDate = lastYearCalendar.time


                    formDates(
                        startDate = startDate,
                        endDate = endDate,
                        param = "last_year",
                        selectedText = radioButtonLastYear.text.toString()
                    )


                }
                R.id.radio_button_penultimate_year -> {

                    val penultimateYearCalendar = Calendar.getInstance()
                    penultimateYearCalendar.add(Calendar.YEAR, -2)
                    penultimateYearCalendar[Calendar.MONTH] = Calendar.JANUARY
                    val startDate = penultimateYearCalendar.time

                    penultimateYearCalendar[Calendar.MONTH] = Calendar.DECEMBER
                    val endDate = penultimateYearCalendar.time

                    formDates(
                        startDate = startDate,
                        endDate = endDate,
                        param = "penultimate_year",
                        selectedText = radioButtonPenultimateYear.text.toString()
                    )
                }

            }


        }


        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
//        txt_scan.setOnClickListener {
//            dismissAllowingStateLoss()
//            mListener?.onItemClick("scan")
//
//        }
//
//        txt_search.setOnClickListener {
//            dismissAllowingStateLoss()
//            mListener?.onItemClick("search")
//        }
    }


    private fun formDates(
        startDate: Date,
        endDate: Date,
        param: String,
        selectedText: String
    ) {


      val  fromDate = startDate.toString("MM-dd-yyyy")
     val   toDate = endDate.toString("MM-dd-yyyy")
//        val fromDate = "${startMonth}-01-$startYear"
//        val toDate = "${endMonth}-01-$endYear"

        dismissAllowingStateLoss()
        mListener?.onItemClick(fromDate, toDate, param, selectedText)
    }

    private var mListener: ItemClickListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context

            arguments?.let {
                selectedTimeFilter = it.getString(ARG_PARAM) ?: "last_3_months"

            }


        } else {
            throw RuntimeException(
                context.toString()
                    .toString() + " must implement ItemClickListener"
            )
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mListener = parentFragment as ItemClickListener?
////        if (context is ItemClickListener) {
////            mListener = context as ItemClickListener
////        } else {
////            throw RuntimeException(
////                context.toString() + " must implement ItemClickListener"
////            )
////        }
//    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface ItemClickListener {
        fun onItemClick(fromDate: String, toDate: String, param: String, selectedText: String)
    }

    companion object {

        private const val ARG_PARAM = "param"

        @JvmStatic
        fun newInstance(param: String): FilterDatesBottomSheetFragment {
            val fragment = FilterDatesBottomSheetFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_PARAM, param)

            }

            return fragment
        }
    }
}