package com.goldmedal.crm.ui.dashboard.home

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.R
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.common.LocationManager.GPSPoint
import com.goldmedal.crm.common.LocationManager.Wherebout
import com.goldmedal.crm.common.LocationManager.Workable
import com.goldmedal.crm.data.model.GetAcceptedTicketData
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.goldmedal.crm.data.model.GetAppointmentsData
import com.goldmedal.crm.databinding.ActivityUpcomingAppointmentsBinding
import com.goldmedal.crm.ui.ticket.*
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.util.toString
import com.goldmedal.crm.util.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class AppointmentsActivity : AppCompatActivity(), KodeinAware, DashboardApiListener<Any>,
    IStatusListener, View.OnClickListener {



    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityUpcomingAppointmentsBinding

//    private lateinit var startDate: Calendar
//    private lateinit var endDate: Calendar

    private lateinit var minEndDate: Calendar
    private lateinit var maxStartDate: Calendar

    private var  strEndDate = ""
    private var  strStartDate = ""

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpcomingAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.apiListener = this


        Wherebout(this).onChange(object : Workable<GPSPoint?> {
            override fun work(t: GPSPoint?) {
                // draw something in the UI with this new data
                latitude = t?.latitude
                longitude = t?.longitude
            }
        })

//        maxStartDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        startDate.add(Calendar.DAY_OF_MONTH, -7)
        minEndDate = startDate

        val today = startDate.time
        strStartDate = today.toString("MM-dd-yyyy")
//        startDate.add(Calendar.DAY_OF_MONTH, 1)

//        val mYear = startDate[Calendar.YEAR]
//        val mMonth = startDate[Calendar.MONTH]
//        val mDay = startDate[Calendar.DAY_OF_MONTH]


        //end after 1 week from now
       val  endDate = Calendar.getInstance()
        endDate.add(Calendar.DAY_OF_MONTH, 7)

        maxStartDate = endDate


        val next = endDate.time

//        minEndDate = calendar
        strEndDate = next.toString("MM-dd-yyyy")


        binding.layoutFromToDate.tvSelectStartDate.text = today.toString("dd/MM/yyyy")
        binding.layoutFromToDate.tvSelectEndDate.text = next.toString("dd/MM/yyyy")

        binding.layoutFromToDate.layoutStartDate.setOnClickListener(this)
        binding.layoutFromToDate.layoutEndDate.setOnClickListener(this)


        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getAppointments(user.UserId,strStartDate,strEndDate)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REFRESH_REQUEST_CODE && resultCode == REFRESH_RESULT_CODE) {
            viewModel.getLoggedInUser().observe(this, Observer { user ->
                if (user != null) {
                   // viewModel.getTicketsCount(user.UserId)
                    viewModel.getAppointments(user.UserId,strStartDate,strEndDate)
                  //  viewModel.getDashboardData(user.UserId)
                }
            })
        }
    }
    private fun List<GetAppointmentsData?>.toAppointments(): List<AppointmentsItem?> {
        return this.map {
            AppointmentsItem(
                it,
                this@AppointmentsActivity,
                this@AppointmentsActivity,
                latitude,
                longitude
            )
        }
    }


    private fun bindUI(list: List<GetAppointmentsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toAppointments())
        }


    }

    private fun initRecyclerView(toLeaveRecord: List<AppointmentsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveRecord)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted(callFrom: String) {
binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String, timestamp: String) {

        binding.viewCommon.hide()

        if (callFrom == "today_appointment") {
            val data = _object as List<GetAppointmentsData?>
            if (data.isNullOrEmpty()) {
                binding.viewCommon.showNoData()
            }
            bindUI(data)

        }

        if (callFrom == "ticket_details") {
            val data = _object as List<GetTicketDetailsData?>

            if (!data.isNullOrEmpty()) {
                if (data[0]?.IsCheckedIn == 0) {

                    val intent = Intent(this, CheckInActivity::class.java)
                    intent.putExtra(ARG_PARAM, data[0])
                    startActivityForResult(intent, REFRESH_REQUEST_CODE)

                   // CheckInActivity.start(this, item = data[0])
                } else {

                    val intent = Intent(this, TicketInfoActivity::class.java)
                    intent.putExtra(ARG_PARAM, data[0])
                    startActivityForResult(intent, REFRESH_REQUEST_CODE)
                  //  TicketInfoActivity.start(this, data[0])

                }
            }
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        if(isNetworkError){
            binding.viewCommon.showNoInternet()
        }else{
            binding.viewCommon.showNoData()
        }

        bindUI(ArrayList())

        toast(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        toast(message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        private const val ARG_PARAM = "model_item"
        private const val REFRESH_REQUEST_CODE = 101
        private const val REFRESH_RESULT_CODE = 102
        fun start(context: Context) {
            context.startActivity(Intent(context, AppointmentsActivity::class.java))
        }
    }

    override fun observeCheckInStatus(ticketId: Int?) {
        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getTicketDetails(user.UserId, ticketId ?: -1)
            }
        })
    }

    override fun cancelTicket(ticketData: GetAcceptedTicketData?, actionId: Int) {

    }


    override fun onClick(viewId: View?) {
        //        val mYear: Int
//        val mMonth: Int
//        val mDay: Int
        when (viewId?.id) {
//            R.id.layout_startDate -> {
            R.id.layout_startDate -> {


                // Get Current Date
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]


                val startDatePicker = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        binding.layoutFromToDate.tvSelectStartDate.text = String.format(
                            Locale.getDefault(),
                            "%s/%d/%d",
                            dayOfMonth.toString(),
                            monthOfYear + 1,
                            year
                        )
                        minEndDate.set(year, monthOfYear, dayOfMonth)

                        strStartDate = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year

//                        if (strEndDate.isNullOrEmpty()) {
//                            binding.layoutServiceTickets.rootLayout.snackbar("Please Select End Date")
//                        }

//                        else {


                            viewModel.getLoggedInUser().observe(this, Observer { user ->

                                if (user != null) {
                                    viewModel.getAppointments(
                                        user.UserId,
                                        strStartDate,
                                        strEndDate
                                    )
                                }
                            })
//                        }

                    }, mYear, mMonth, mDay
                )

                // startDatePicker.datePicker.minDate = c.timeInMillis

                if (strEndDate.isNotEmpty()) {
                    startDatePicker.datePicker.maxDate = maxStartDate.timeInMillis
                }

                startDatePicker.show()

            }
            R.id.layout_endDate -> {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]

                val endDatePicker = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfMonth ->
                        binding.layoutFromToDate.tvSelectEndDate.text = String.format(
                            Locale.getDefault(),
                            "%d/%d/%d",
                            dayOfMonth,
                            monthOfYear + 1,
                            year
                        )
                        maxStartDate.set(year, monthOfYear, dayOfMonth)
                        strEndDate = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
//                        if (strStartDate.isNullOrEmpty()) {
//                            binding.layoutServiceTickets.rootLayout.snackbar("Please Select Start Date")
//                        }
//                        else {
                            viewModel.getLoggedInUser().observe(this, Observer { user ->

                                if (user != null) {
                                    viewModel.getAppointments(
                                        user.UserId,
                                        strStartDate,
                                        strEndDate
                                    )
                                }
                            })
//                        }
                    }, mYear, mMonth, mDay
                )
                if (strStartDate.isNotEmpty()) {
                    endDatePicker.datePicker.minDate = minEndDate.timeInMillis
                }
//                else {
//                    //endDatePicker.datePicker.minDate = c.timeInMillis
//                }

                //   endDatePicker.datePicker.maxDate = c.timeInMillis
                endDatePicker.show()
            }
        }
    }


}