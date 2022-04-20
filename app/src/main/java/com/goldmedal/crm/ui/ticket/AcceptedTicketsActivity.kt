package com.goldmedal.crm.ui.ticket


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
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
import com.goldmedal.crm.databinding.ActivityAcceptedTicketBinding
import com.goldmedal.crm.util.*
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.hrapp.ui.dialogs.TicketCloseDialog
import com.goldmedal.hrapp.ui.dialogs.TicketRescheduleDialog
import com.goldmedal.hrapp.ui.dialogs.TicketUnacceptanceDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_accepted_ticket.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_STATUS_BY = "status_by"
private const val ARG_MODEL_ITEM = "model_item"
private const val TAG = "AcceptedTicketsActivity"

// - - - - - actionid ** ** 1 = "Re-Assign", 2 = "Re-Schedule", 3 = "Closed" , 5 - "Outside reassign" , 4 - "Outside Re-Schedule" ,6 - "Outside Closed"  - - - - - - - - -
class AcceptedTicketsActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any>,
    View.OnClickListener, IStatusListener, TicketUnacceptanceDialog.OnRejectTicket,
    TicketCloseDialog.OnCloseReceived, TicketRescheduleDialog.OnRescheduleReceived {

    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()

    private lateinit var viewModel: TicketViewModel
    private var statusBy: Int? = null

    var strStartDate: String? = null
    var strEndDate: String? = null
    var strSearchBy: String = ""

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0
    var action_id = -1

//    private var ticketID: Int? = null

    private lateinit var minEndDate: Calendar
    private lateinit var maxStartDate: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityAcceptedTicketBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_accepted_ticket)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        binding.viewmodelAcceptedTicket = viewModel
        viewModel.apiListener = this

//        ticketID = intent.getIntExtra("TicketID",0)
//
//        if (ticketID!= null && ticketID != 0) {
//            viewModel.getLoggedInUser().observe(this, Observer { user ->
//                if (user != null) {
//                    viewModel.getTicketDetails(user.UserId, ticketID!!)
//                }
//            })
//        }

        layout_startDate?.setOnClickListener(this)
        layout_endDate?.setOnClickListener(this)

        maxStartDate = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        val today = calendar.time
        strEndDate = today.toString("MM-dd-yyyy")

        calendar.add(Calendar.MONTH, -1)
        val previous = calendar.time

        minEndDate = calendar
        strStartDate = previous.toString("MM-dd-yyyy")


        tvSelectStartDate?.text = previous.toString("dd/MM/yyyy")
        tvSelectEndDate?.text = today.toString("dd/MM/yyyy")

        intent?.let {
            statusBy = it.getIntExtra(ARG_STATUS_BY, 0)
            when (statusBy) {
                1 ->
                    supportActionBar?.title = "All Tickets"
                2 ->
                    supportActionBar?.title = "Urgent Tickets"
                3 ->
                    supportActionBar?.title = "In-Progress Tickets"
                4 ->
                    supportActionBar?.title = "Pending Tickets"
                5 ->
                    supportActionBar?.title = "Un-Paid Tickets"
                6 ->
                    supportActionBar?.title = "Paid Tickets"
            }
        }


//Refresh List
        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getAcceptedTickets(
                    user.UserId,
                    strStartDate ?: Date().toString("MM-dd-yyyy"),
                    strEndDate ?: Date().toString("MM-dd-yyyy"),
                    statusBy ?: 0,
                    strSearchBy
                )
            }
        })


//SearchView
        search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                        .observe(this@AcceptedTicketsActivity, Observer { user ->

                            if (user != null) {
                                viewModel.getAcceptedTickets(
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

        search_view?.setOnCloseListener {
            strSearchBy = ""
            viewModel.getLoggedInUser().observe(this, Observer { user ->

                if (user != null) {
                    viewModel.getAcceptedTickets(
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


    private fun List<GetAcceptedTicketData?>.acceptedData(): List<AcceptedTicketItem?> {
        return this.map {
            AcceptedTicketItem(
                it,
                this@AcceptedTicketsActivity,
                this@AcceptedTicketsActivity,
                statusBy
            )
        }
    }

    private fun initRecyclerView(acceptedData: List<AcceptedTicketItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(acceptedData)
        }

        rvList?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<GetAcceptedTicketData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.acceptedData())
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id
//        val mYear: Int
//        val mMonth: Int
//        val mDay: Int
        when (id) {
            R.id.layout_startDate -> {


                // Get Current Date
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]


                val startDatePicker = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        tvSelectStartDate.text = String.format(
                            Locale.getDefault(),
                            "%s/%d/%d",
                            dayOfMonth.toString(),
                            monthOfYear + 1,
                            year
                        )
                        minEndDate.set(year, monthOfYear, dayOfMonth)

                        strStartDate = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year

                        if (strEndDate.isNullOrEmpty()) {
                            root_layout?.snackbar("Please Select End Date")
                        } else {


                            viewModel.getLoggedInUser().observe(this, Observer { user ->

                                if (user != null) {
                                    viewModel.getAcceptedTickets(
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
                        tvSelectEndDate.text = String.format(
                            Locale.getDefault(),
                            "%d/%d/%d",
                            dayOfMonth,
                            monthOfYear + 1,
                            year
                        )
                        maxStartDate.set(year, monthOfYear, dayOfMonth)
                        strEndDate = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
                        if (strStartDate.isNullOrEmpty()) {
                            root_layout?.snackbar("Please Select Start Date")
                        } else {
                            viewModel.getLoggedInUser().observe(this, Observer { user ->

                                if (user != null) {
                                    viewModel.getAcceptedTickets(
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

                c.add(Calendar.MONTH, 1);
                endDatePicker.datePicker.maxDate = c.timeInMillis
                endDatePicker.show()
            }

        }


    }


    override fun onStarted(callFrom: String) {
        view_common?.showProgressBar()
        Log.d(TAG, "onStarted: HIT")
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        view_common?.hide()

        if (callFrom == "accepted_tickets") {

            val data = _object as List<GetAcceptedTicketData?>
            if (data.isNullOrEmpty()) {
                view_common?.showNoData()
            }
            bindUI(data)
        }




        if (callFrom == "ticket_details") {
            val data = _object as List<GetTicketDetailsData?>

            if (!data.isNullOrEmpty()) {
                if (data[0]?.IsCheckedIn == 0) {

//                    val intent = Intent(this, CheckInActivity::class.java)
//                    intent.putExtra(ARG_PARAM_ITEM, data[0])
//                    startActivityForResult(intent, REFRESH_REQUEST_CODE)

                    val intent = Intent(this, CheckInActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                    intent.putExtra(ARG_MODEL_ITEM, data[0])
                    startActivity(intent)
                    finish()

//     CheckInActivity.start(this, item = data[0])

                } else {
//                    val intent = Intent(this, TicketInfoActivity::class.java)
//                    intent.putExtra(ARG_PARAM_ITEM, data[0])
//                    startActivityForResult(intent, REFRESH_REQUEST_CODE)

                    val intent = Intent(this, TicketInfoActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                    intent.putExtra(ARG_MODEL_ITEM, data[0])
                    startActivity(intent)
                    finish()
                    //  TicketInfoActivity.start(this, data[0])

                }
            }

        }

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            view_common?.showNoInternet()
        } else {
            view_common?.showNoData()
        }

        bindUI(ArrayList())
        root_layout?.snackbar(message)

    }

    override fun onValidationError(message: String, callFrom: String) {
        root_layout?.snackbar(message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        //        private const val REFRESH_REQUEST_CODE = 101
//        private const val REFRESH_RESULT_CODE = 102
        private const val ARG_PARAM_ITEM = "model_item"
        fun start(context: Context, statusBy: Int) {
            val intent = Intent(context, AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, statusBy)
            context.startActivity(intent)
        }


//        fun startRefresh(context: Context, ticketID: Int) {
//            val intent = Intent(context, AcceptedTicketsActivity::class.java)
//            intent.putExtra("TicketID", ticketID)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            context.startActivity(intent)
//        }
    }

    override fun observeCheckInStatus(ticketId: Int?) {
        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getTicketDetails(user.UserId, ticketId ?: -1)
            }
        })
    }

    override fun cancelTicket(ticketData: GetAcceptedTicketData?, actionId: Int) {
        action_id = actionId
        Wherebout(this).onChange(object : Workable<GPSPoint?> {
            override fun work(t: GPSPoint?) {
                // draw something in the UI with this new data
                latitude = t?.latitude
                longitude = t?.longitude

                if (actionId == 4) {
                    MaterialAlertDialogBuilder(this@AcceptedTicketsActivity)
                        .setTitle("Ticket No: ${ticketData?.Tktno}")
                        .setMessage(resources.getString(R.string.supporting_text_reschedule))
                        .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
                        .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->

                            ticketData?.let { showRescheduleTicketDialog(it) }
                        }
                        .show()

                } else if (actionId == 6) {
                    MaterialAlertDialogBuilder(this@AcceptedTicketsActivity)
                        .setTitle("Ticket No: ${ticketData?.Tktno}")
                        .setMessage(resources.getString(R.string.supporting_text_close))
                        .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
                        .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->

                            ticketData?.let { showCloseTicketDialog(it) }
                        }
                        .show()

                } else if (actionId == 5) {
                    MaterialAlertDialogBuilder(this@AcceptedTicketsActivity)
                        .setTitle("Ticket No: ${ticketData?.Tktno}")
                        .setMessage(resources.getString(R.string.supporting_text_reassign))
                        .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
                        .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->

                            ticketData?.let { showCloseTicketDialog(it) }
                        }
                        .show()

                } else {
                    MaterialAlertDialogBuilder(this@AcceptedTicketsActivity)
                        .setTitle("Ticket No: ${ticketData?.Tktno}")
                        .setMessage(resources.getString(R.string.supporting_text_reject))
                        .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
                        .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->

                            ticketData?.let { showRejectTicketDialog(it) }
                        }
                        .show()

                }
            }
        })

    }


    private fun showRescheduleTicketDialog(modelItem: GetAcceptedTicketData) {

        val dialogFragment = TicketRescheduleDialog.newInstance()
        dialogFragment.callBack = this
        val bundle = Bundle()
        bundle.putInt("ticketId", modelItem.TicketID ?: -1)
        bundle.putString("ticketNo", modelItem.Tktno ?: "")
        bundle.putString("deviceLatitude", latitude.toString())
        bundle.putString("deviceLongitude", longitude.toString())
        bundle.putString(
            "deviceAddress",
            getAddressFromLatLong(this, latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road"
        )
        dialogFragment.arguments = bundle
        val ft = this.supportFragmentManager.beginTransaction()
        val prev = this.supportFragmentManager.findFragmentByTag("reschedule_tkt_dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment.show(ft, "reschedule_tkt_dialog")

    }


    private fun showRejectTicketDialog(modelItem: GetAcceptedTicketData) {
        val dialogFragment = TicketUnacceptanceDialog.newInstance()
        dialogFragment.callBack = this
        val bundle = Bundle()
        bundle.putInt("ticketNo", modelItem.TicketID ?: -1)
        bundle.putInt("type", 2)
        bundle.putString("deviceLatitude", latitude.toString())
        bundle.putString("deviceLongitude", longitude.toString())
        bundle.putString(
            "deviceAddress",
            getAddressFromLatLong(this, latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road"
        )
        dialogFragment.arguments = bundle
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("reject_tkt_dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment.show(ft, "reject_tkt_dialog")

    }

    private fun showCloseTicketDialog(modelItem: GetAcceptedTicketData) {
        val dialogFragment = TicketCloseDialog.newInstance()
        dialogFragment.callBack = this
        val bundle = Bundle()
        bundle.putInt("ticketId", modelItem?.TicketID ?: -1)
        bundle.putInt("actionId", action_id)
        bundle.putString("ticketNo", modelItem?.Tktno ?: "")
        bundle.putString("deviceLatitude", latitude.toString())
        bundle.putString("deviceLongitude", longitude.toString())
        bundle.putString(
            "deviceAddress",
            getAddressFromLatLong(this, latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road"
        )
        dialogFragment.arguments = bundle
        val ft = this.supportFragmentManager.beginTransaction()
        val prev = this.supportFragmentManager.findFragmentByTag("close_tkt_dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        dialogFragment.show(ft, "close_tkt_dialog")
    }

    override fun onRejectTkt() {
        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getAcceptedTickets(
                    user.UserId,
                    strStartDate ?: Date().toString("MM-dd-yyyy"),
                    strEndDate ?: Date().toString("MM-dd-yyyy"),
                    statusBy ?: 0,
                    strSearchBy
                )
            }
        })
    }

    override fun onCloseReceived() {
        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getAcceptedTickets(
                    user.UserId,
                    strStartDate ?: Date().toString("MM-dd-yyyy"),
                    strEndDate ?: Date().toString("MM-dd-yyyy"),
                    statusBy ?: 0,
                    strSearchBy
                )
            }
        })
    }

    override fun onRescheduleReceived() {
        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getAcceptedTickets(
                    user.UserId,
                    strStartDate ?: Date().toString("MM-dd-yyyy"),
                    strEndDate ?: Date().toString("MM-dd-yyyy"),
                    statusBy ?: 0,
                    strSearchBy
                )
            }
        })
    }


}