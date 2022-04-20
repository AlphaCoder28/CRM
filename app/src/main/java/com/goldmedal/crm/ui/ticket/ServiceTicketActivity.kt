package com.goldmedal.crm.ui.ticket


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.common.LocationManager.GPSPoint
import com.goldmedal.crm.common.LocationManager.Wherebout
import com.goldmedal.crm.common.LocationManager.Workable
import com.goldmedal.crm.data.model.GetAcceptedTicketData
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.goldmedal.crm.data.model.GetTicketDetailsForEngineerData
import com.goldmedal.crm.data.model.GetTicketsCountData
import com.goldmedal.crm.databinding.ActivityServiceTicketBinding
import com.goldmedal.crm.databinding.ServiceTicketsInfoBinding
import com.goldmedal.crm.util.*
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.util.interfaces.OnServiceTicketClickListener
import com.goldmedal.hrapp.ui.dialogs.TicketCloseDialog
import com.goldmedal.hrapp.ui.dialogs.TicketRescheduleDialog
import com.goldmedal.hrapp.ui.dialogs.TicketUnacceptanceDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


private const val ARG_PARAM = "model_item"
private const val TAG = "ServiceTicketActivity"
class ServiceTicketActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any>, View.OnClickListener ,IStatusListener{


    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()

    private lateinit var viewModel: TicketViewModel
    private var statusBy: Int? = null

    var strStartDate: String? = null
    var strEndDate: String? = null
    var strSearchBy: String = ""

    private lateinit var minEndDate: Calendar
    private lateinit var maxStartDate: Calendar

    private var modelItem: GetTicketsCountData? = null

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    var action_id = -1


    private lateinit var binding: ServiceTicketsInfoBinding
//    private var modelItem: GetTicketDetailsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ServiceTicketsInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
//        binding.viewmodelAcceptedTicket = viewModel

        viewModel.apiListener = this

        binding.layoutStartDate.setOnClickListener(this)
        binding.layoutEndDate.setOnClickListener(this)

        maxStartDate = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        val today = calendar.time
        strEndDate = today.toString("MM-dd-yyyy")

        calendar.add(Calendar.MONTH, -1)
        val previous = calendar.time

        minEndDate = calendar
        strStartDate = previous.toString("MM-dd-yyyy")


        binding.tvSelectStartDate.text = previous.toString("dd/MM/yyyy")
        binding.tvSelectEndDate.text = today.toString("dd/MM/yyyy")


        intent?.let {
            modelItem = it.getParcelableExtra(ARG_PARAM)
            if (modelItem != null) {
                supportActionBar?.title = modelItem?.TicketName
                statusBy = modelItem?.TicketId
            }
        }


//SearchView


        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                strSearchBy = newText
                //  filter(newText);
                if (newText.trim { it <= ' ' }.isEmpty() || newText.trim { it <= ' ' }.length > 1) {
                    // currentPage = 0
                    // newArrDispatchedMaterial.clear()

                    viewModel.getLoggedInUser()
                        .observe(this@ServiceTicketActivity, Observer { user ->

                            if (user != null) {
                                viewModel.getTicketDetailsForEngineer(
                                    user.UserId,
                                    strStartDate ?: Date().toString("MM-dd-yyyy"),
                                    strEndDate ?: Date().toString("MM-dd-yyyy"),
                                    statusBy ?: 0,
                                    strSearchBy
                                )
                            }
                        })
                }
                return true
            }
        })
        binding.searchView.setOnCloseListener {
            strSearchBy = ""
            viewModel.getLoggedInUser().observe(this, Observer { user ->

                if (user != null) {
                    viewModel.getTicketDetailsForEngineer(
                        user.UserId,
                        strStartDate ?: Date().toString("MM-dd-yyyy"),
                        strEndDate ?: Date().toString("MM-dd-yyyy"),
                        statusBy ?: 0,
                        strSearchBy
                    )
                }
            })
            false
        }


    }


    override fun onResume() {
        super.onResume()

        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getTicketDetailsForEngineer(
                    user.UserId,
                    strStartDate ?: Date().toString("MM-dd-yyyy"),
                    strEndDate ?: Date().toString("MM-dd-yyyy"),
                    statusBy ?: 0,
                    strSearchBy
                )
            }
        })
    }



    private fun List<GetTicketDetailsForEngineerData?>.serviceTicketsData(): List<ServiceTicketItem?> {
        return this.map {
            ServiceTicketItem(it, this@ServiceTicketActivity, null,this@ServiceTicketActivity,statusBy ?: 0)
        }
    }

    private fun initRecyclerView(serviceTicketsData: List<ServiceTicketItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(serviceTicketsData)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<GetTicketDetailsForEngineerData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.serviceTicketsData())
        }
    }


    companion object {
        fun start(
            context: Context,
            item: GetTicketsCountData?
        ) {
            context.startActivity(
                Intent(context, ServiceTicketActivity::class.java)
                    .putExtra(ARG_PARAM, item)
            )
        }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.viewCommon.hide()

        if (callFrom == "service_tickets") {

            val data = _object as List<GetTicketDetailsForEngineerData?>
            if (data.isNullOrEmpty()) {
                binding.viewCommon.showNoData()
            }
            bindUI(data)
        }




        if (callFrom == "ticket_details") {
            val data = _object as List<GetTicketDetailsData?>

            if (!data.isNullOrEmpty()) {
                if (data[0]?.IsCheckedIn == 0) {
                    CheckInActivity.start(this, item = data[0])

                } else {
                    TicketInfoActivity.start(this, data[0])

                }
            }

        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            binding.viewCommon.showNoInternet()
        } else {
            binding.viewCommon.showNoData()
        }

        bindUI(ArrayList())
        binding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }

    override fun onClick(p0: View?) {
        val id = p0?.id
//        val mYear: Int
//        val mMonth: Int
//        val mDay: Int
        when (id) {
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
                        binding.tvSelectStartDate.text = String.format(
                            Locale.getDefault(),
                            "%s/%d/%d",
                            dayOfMonth.toString(),
                            monthOfYear + 1,
                            year
                        )
                        minEndDate.set(year, monthOfYear, dayOfMonth)

                        strStartDate = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year

                        if (strEndDate.isNullOrEmpty()) {
                            binding.rootLayout.snackbar("Please Select End Date")
                        } else {


                            viewModel.getLoggedInUser().observe(this, Observer { user ->

                                if (user != null) {
                                    viewModel.getTicketDetailsForEngineer(
                                        user.UserId,
                                        strStartDate ?: Date().toString("MM-dd-yyyy"),
                                        strEndDate ?: Date().toString("MM-dd-yyyy"),
                                        statusBy ?: 0,
                                        strSearchBy
                                    )
                                }
                            })
                        }

                    }, mYear, mMonth, mDay
                )

                // startDatePicker.datePicker.minDate = c.timeInMillis

                if (strEndDate?.isNotEmpty() == true) {
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
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        binding.tvSelectEndDate.text = String.format(
                            Locale.getDefault(),
                            "%d/%d/%d",
                            dayOfMonth,
                            monthOfYear + 1,
                            year
                        )
                        maxStartDate.set(year, monthOfYear, dayOfMonth)
                        strEndDate = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
                        if (strStartDate.isNullOrEmpty()) {
                            binding.rootLayout.snackbar("Please Select Start Date")
                        } else {
                            viewModel.getLoggedInUser().observe(this, Observer { user ->

                                if (user != null) {
                                    viewModel.getTicketDetailsForEngineer(
                                        user.UserId,
                                        strStartDate ?: Date().toString("MM-dd-yyyy"),
                                        strEndDate ?: Date().toString("MM-dd-yyyy"),
                                        statusBy ?: 0,
                                        strSearchBy
                                    )
                                }
                            })
                        }
                    }, mYear, mMonth, mDay
                )
                if (strStartDate?.isNotEmpty() == true) {
                    endDatePicker.datePicker.minDate = minEndDate.timeInMillis
                } else {
                    //endDatePicker.datePicker.minDate = c.timeInMillis
                }

             //   endDatePicker.datePicker.maxDate = c.timeInMillis
                endDatePicker.show()
            }

        }
    }

//    override fun onTicketClick(model: GetTicketDetailsForEngineerData?, actionId: Int) {
//        action_id = actionId
//        Wherebout(this).onChange(object : Workable<GPSPoint?> {
//            override fun work(t: GPSPoint?) {
//                // draw something in the UI with this new data
//                latitude = t?.latitude
//                longitude = t?.longitude
//
//                if(actionId == 5 || actionId == 6){
//                    MaterialAlertDialogBuilder(this@ServiceTicketActivity)
//                            .setTitle("Ticket No: ${model?.Tktno}")
//                            .setMessage(resources.getString(R.string.supporting_text_close))
//                            .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
//                            .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
//
//                                model?.let { showCloseTicketDialog(it) }
//                            }
//                            .show()
//
//                }else{
//                    MaterialAlertDialogBuilder(this@ServiceTicketActivity)
//                            .setTitle("Ticket No: ${model?.Tktno}")
//                            .setMessage(resources.getString(R.string.supporting_text_reject))
//                            .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
//                            .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
//
//                                model?.let { showRejectTicketDialog(it) }
//                            }
//                            .show()
//
//                }
//            }
//        })
//
//    }


//    private fun showRejectTicketDialog(modelItem: GetTicketDetailsForEngineerData?) {
//        val dialogFragment = TicketUnacceptanceDialog.newInstance()
//        dialogFragment.callBack = this
//        val bundle = Bundle()
//        bundle.putInt("ticketNo", modelItem?.TicketID ?: -1)
//        bundle.putInt("type", 2)
//        bundle.putString("deviceLatitude",latitude.toString())
//        bundle.putString("deviceLongitude",longitude.toString())
//        bundle.putString("deviceAddress", getAddressFromLatLong(this, latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road")
//
//        dialogFragment.arguments = bundle
//        val ft = supportFragmentManager.beginTransaction()
//        val prev = supportFragmentManager.findFragmentByTag("reject_tkt_dialog")
//        if (prev != null) {
//            ft.remove(prev)
//        }
//        ft.addToBackStack(null)
//        dialogFragment.show(ft, "reject_tkt_dialog")
//
//    }


//    private fun showCloseTicketDialog(modelItem: GetTicketDetailsForEngineerData?) {
//        val dialogFragment = TicketCloseDialog.newInstance()
//        dialogFragment.callBack = this
//        val bundle = Bundle()
//        bundle.putInt("ticketId", modelItem?.TicketID ?: -1)
//        bundle.putInt("actionId", action_id)
//        bundle.putString("ticketNo", modelItem?.Tktno ?: "")
//        bundle.putString("deviceLatitude",latitude.toString())
//        bundle.putString("deviceLongitude",longitude.toString())
//        bundle.putString("deviceAddress",getAddressFromLatLong(this, latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road")
//        dialogFragment.arguments = bundle
//        val ft = this.supportFragmentManager.beginTransaction()
//        val prev = this.supportFragmentManager.findFragmentByTag("close_tkt_dialog")
//        if (prev != null) {
//            ft.remove(prev)
//        }
//        ft.addToBackStack(null)
//        dialogFragment.show(ft, "close_tkt_dialog")
//
//    }

//    override fun onRejectTkt() {
//        viewModel.getLoggedInUser().observe(this, Observer { user ->
//
//            if (user != null) {
//                viewModel.getTicketDetailsForEngineer(
//                        user.UserId,
//                        strStartDate ?: Date().toString("MM-dd-yyyy"),
//                        strEndDate ?: Date().toString("MM-dd-yyyy"),
//                        statusBy ?: 0,
//                        strSearchBy
//                )
//            }
//        })
//    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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


}