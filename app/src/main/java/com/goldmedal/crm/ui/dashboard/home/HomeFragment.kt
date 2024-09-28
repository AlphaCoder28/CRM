package com.goldmedal.crm.ui.dashboard.home


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.goldmedal.crm.R
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.common.FigureIndicatorView
import com.goldmedal.crm.common.LocationManager.GPSPoint
import com.goldmedal.crm.common.LocationManager.Wherebout
import com.goldmedal.crm.common.LocationManager.Workable
import com.goldmedal.crm.common.PeekingLinearLayoutManager
import com.goldmedal.crm.data.adapters.VerticalBannerAdapter
import com.goldmedal.crm.data.model.*
import com.goldmedal.crm.data.network.GlobalConstant.DEFAULT_DATE
import com.goldmedal.crm.databinding.HomeFragmentBinding
import com.goldmedal.crm.ui.customers.ContactsActivity
import com.goldmedal.crm.ui.parts.AvailablePartsActivity
import com.goldmedal.crm.ui.parts.PartsRequirementActivity
import com.goldmedal.crm.ui.parts.UsedPartsActivity
import com.goldmedal.crm.ui.ticket.AcceptedTicketsActivity
import com.goldmedal.crm.ui.ticket.CheckInActivity
import com.goldmedal.crm.ui.ticket.TicketInfoActivity
import com.goldmedal.crm.util.*
import com.goldmedal.crm.util.interfaces.AcceptRejectTicketsListener
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.ui.dialogs.TicketUnacceptanceDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.base.IIndicator
import com.zhpan.indicator.enums.IndicatorSlideMode
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

private const val ARG_PARAM = "model_item"
private const val ARG_STATUS_BY = "status_by"

class HomeFragment : Fragment(), KodeinAware, DashboardApiListener<Any>,
    AcceptRejectTicketsListener,
    TicketUnacceptanceDialog.OnRejectTicket, IStatusListener{

    var mContext: Context? = null
    override val kodein by kodein()

    private val factory: HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel

    private lateinit var homeFragmentBinding: HomeFragmentBinding

    private var strDate = ""

    private lateinit var verticalAssignedTicketsBanner: BannerViewPager<GetAllAssignedTicketsData?>

    private lateinit var mAppointmentAdapter: GroupAdapter<GroupieViewHolder>

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return homeFragmentBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        homeFragmentBinding.viewmodelHome = viewModel

        verticalAssignedTicketsBanner =
            requireView().findViewById(R.id.verticalAssignedTicketsBanner)


        val calendar = Calendar.getInstance()
        val today = calendar.time
        strDate = today.toString("MM-dd-yyyy")

//API Call
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.getAllAssignedTickets(user.UserId)
                viewModel.getTicketsCount(user.UserId, DEFAULT_DATE, DEFAULT_DATE)
                viewModel.getAppointments(user.UserId, strDate, strDate)
                viewModel.getDashboardData(user.UserId)
            }
        })


        viewModel.apiListener = this
        initVerticalBanner()
        //getLocationPermission()


        homeFragmentBinding.layoutAllTickets.setOnClickListener {

            val intent = Intent(requireContext(), AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, 1)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)
//            AcceptedTicketsActivity.start(requireContext(), 1)
        }

        homeFragmentBinding.layoutUrgentTickets.setOnClickListener {
            val intent = Intent(requireContext(), AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, 2)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)

//            AcceptedTicketsActivity.start(requireContext(), 2)
        }

        homeFragmentBinding.layoutInProgressTickets.setOnClickListener {
            val intent = Intent(requireContext(), AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, 3)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)
//            AcceptedTicketsActivity.start(requireContext(), 3)
        }

        homeFragmentBinding.layoutPendingTickets.setOnClickListener {
            val intent = Intent(requireContext(), AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, 4)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)
//            AcceptedTicketsActivity.start(requireContext(), 4)
        }


        homeFragmentBinding.layoutUnpaidTickets.setOnClickListener {
            val intent = Intent(requireContext(), AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, 5)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)
//            AcceptedTicketsActivity.start(requireContext(), 5)
        }

        homeFragmentBinding.layoutPaidTickets.setOnClickListener {
            val intent = Intent(requireContext(), AcceptedTicketsActivity::class.java)
            intent.putExtra(ARG_STATUS_BY, 6)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)
//            AcceptedTicketsActivity.start(requireContext(), 6)
        }


        homeFragmentBinding.layoutContacts.setOnClickListener {
            ContactsActivity.start(requireContext())
        }

        homeFragmentBinding.layoutUsedParts.setOnClickListener {
            UsedPartsActivity.start(requireContext())
        }

        homeFragmentBinding.layoutPartsRequirement.setOnClickListener {
            PartsRequirementActivity.start(requireContext(),"Dashboard",GetTicketDetailsData("","","","","","",
                "","","","",false,0,"",false,"","",
                "","","","","","","","","","",0,
            "","","","","",false,"",0,0,0,"",
            false,"","","","","","","","","",
                "",0,false,"",false,0,false, false, "", "", "",
            "", "", false, 0))
        }

        homeFragmentBinding.layoutAvailableParts.setOnClickListener {
            AvailablePartsActivity.start(requireContext())
        }


        homeFragmentBinding.imvRefresh.setOnClickListener {
            refreshAnimation()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REFRESH_REQUEST_CODE && resultCode == REFRESH_RESULT_CODE) {
            viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    viewModel.getTicketsCount(user.UserId, DEFAULT_DATE, DEFAULT_DATE)
                    viewModel.getAppointments(user.UserId, strDate, strDate)
                    viewModel.getDashboardData(user.UserId)
                }
            })
        }
    }

    private fun lastUpdatedText(timeStamp: String) {

        homeFragmentBinding.txtLastUpdated.text = getString(R.string.last_updated) + formatDateString(timeStamp ?: "", "MM/dd/yyyy hh:mm:ss a", "dd/MM/yyyy hh:mm:ss a")

        if(latitude!! == 0.0 && longitude!! == 0.0){
            mContext?.let {
                Wherebout(it).onChange(object : Workable<GPSPoint?> {
                    override fun work(t: GPSPoint?) {
                        // draw something in the UI with this new data
                        latitude = t?.latitude
                        longitude = t?.longitude

                    }
                })
            }
        }
    }


    private fun refreshAnimation() {
        homeFragmentBinding.imvRefresh.clearAnimation()
        val animation: Animation = RotateAnimation(
            0.0f, 360.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.repeatCount = 0
        animation.duration = 1000
        homeFragmentBinding.imvRefresh.startAnimation(animation)

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.getAllAssignedTickets(user.UserId)
                viewModel.getTicketsCount(user.UserId, DEFAULT_DATE, DEFAULT_DATE)
                viewModel.getAppointments(user.UserId, strDate, strDate)
                viewModel.getDashboardData(user.UserId)
            }
        })
    }

// ============================================================================================
    //  Tickets Count
    // ============================================================================================


    private fun List<GetTicketsCountData?>.toTicketsCount(): List<TicketCountItem?> {
        return this.map {
            mContext?.let { it1 -> TicketCountItem(it, it1) }
        }
    }


    private fun bindTicketsCount(list: List<GetTicketsCountData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toTicketsCount())
        }
    }

    private fun initRecyclerView(toTicketsCount: List<TicketCountItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toTicketsCount)
        }

        homeFragmentBinding.rvList.apply {
            layoutManager =
                PeekingLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

            setHasFixedSize(true)
            //  addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

// ============================================================================================
    //  Today's Appointment
    // ============================================================================================


    private fun List<GetAppointmentsData?>.toTodayAppointment(): List<AppointmentsItem?> {
        return this.map {
            mContext?.let { it1 -> AppointmentsItem(it, it1, this@HomeFragment,latitude,longitude) }
        }
    }


    private fun bindTodaysAppointment(list: List<GetAppointmentsData?>?) = Coroutines.main {
        if (list.isNullOrEmpty()) {
            homeFragmentBinding.llAppointmentTicketMain.visibility = GONE
        } else {
            homeFragmentBinding.llAppointmentTicketMain.visibility = VISIBLE
        }


        list?.let {
            if(latitude!! == 0.0 && longitude!! == 0.0){
                Toast.makeText(requireContext(), "Location not found for today's appointment. Please check your location permission.", Toast.LENGTH_SHORT).show()
            }
            initAppointmentRecyclerView(it.toTodayAppointment())
        }

    }

    private fun initAppointmentRecyclerView(toTodayAppointment: List<AppointmentsItem?>) {

        mAppointmentAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toTodayAppointment)
        }

        homeFragmentBinding.rvAppointmentList
            .apply {
                layoutManager =
                    PeekingLinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

                setHasFixedSize(true)
                //  addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = mAppointmentAdapter
            }
    }


    private fun initVerticalBanner() {
        context.let {
            verticalAssignedTicketsBanner.apply {
                //  setAutoPlay(true)
                setScrollDuration(500)
                setLifecycleRegistry(lifecycle)

                setOrientation(ViewPager2.ORIENTATION_VERTICAL)
                setInterval(7000)
                setPageMargin(0)
                //   setRevealWidth(resources.getDimensionPixelOffset(R.dimen.dp_4), 0)
                adapter = mContext?.resources?.let { it1 ->
                    VerticalBannerAdapter(
                        it1.getDimensionPixelOffset(R.dimen.dp_8),
                        this@HomeFragment,
                        mContext
                    )
                }
                setPageStyle(PageStyle.MULTI_PAGE_SCALE)
            }.create()

        }
    }


    private fun setupCustomIndicator(list: MutableList<GetAllAssignedTicketsData?>) {
        if (list.isEmpty()) {
            homeFragmentBinding.llAssignedTicketMain.visibility = GONE
        }else{
            homeFragmentBinding.llAssignedTicketMain.visibility = VISIBLE
        }

        homeFragmentBinding.indicatorView.visibility = View.INVISIBLE
        try {
            mContext?.resources?.let {
                verticalAssignedTicketsBanner.setAutoPlay(true).setCanLoop(true)
                    .setIndicatorSlideMode(IndicatorSlideMode.NORMAL)
                    .setIndicatorVisibility(View.VISIBLE)
                    .setIndicatorGravity(IndicatorGravity.END)
                    .setIndicatorMargin(
                        0,
                        0,
                        it.getDimensionPixelOffset(R.dimen.dp_16),
                        resources.getDimensionPixelOffset(R.dimen.dp_16)
                    )
                    .setIndicatorView(setupIndicatorView()).refreshData(list)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupIndicatorView(): IIndicator? {
        val indicatorView = FigureIndicatorView(requireContext())
        indicatorView.setRadius(resources.getDimensionPixelOffset(R.dimen.dp_20))
        indicatorView.setTextSize(resources.getDimensionPixelSize(R.dimen.sp_14))
        indicatorView.setBackgroundColor(Color.parseColor("#aa118EEA"))
        return indicatorView
    }

    override fun onStarted(callFrom: String) {

        if (callFrom == "all_assigned_tickets") {
            homeFragmentBinding.assignedTicketsProgressBar.start()
        }
        if (callFrom == "tickets_cnt") {
            homeFragmentBinding.serviceTicketsViewCommon.showProgressBar()
        }

        if (callFrom == "today_appointment") {
            homeFragmentBinding.todaysAppointmentViewCommon.showProgressBar()
        }

        if (callFrom == "dashboard") {
            homeFragmentBinding.dashboardTicketsProgressBar.start()
        }

    }


    override fun onSuccess(_object: List<Any?>, callFrom: String, timestamp: String) {
        if (callFrom == "all_assigned_tickets") {
            homeFragmentBinding.assignedTicketsProgressBar.stop()
           val data = _object as MutableList<GetAllAssignedTicketsData?>
//            if (data.isEmpty()) {
//                llAssignedTicketMain.visibility = GONE
////                val noData = GetAllAssignedTicketsData()
////                noData.ViewType = GlobalConstant.TYPE_NO_DATA
////                data.add(noData)
//            } else {
//                //layout_indicator?.visibility = View.VISIBLE
//                llAssignedTicketMain.visibility = VISIBLE
//            }
            setupCustomIndicator(data)
            lastUpdatedText(timestamp)

            //verticalAssignedTicketsBanner.refreshData(data)
        } else if (callFrom == "tickets_cnt") {
            homeFragmentBinding.serviceTicketsViewCommon.hide()

            bindTicketsCount(_object as List<GetTicketsCountData?>)
            lastUpdatedText(timestamp)
        } else if (callFrom == "today_appointment") {

            homeFragmentBinding.todaysAppointmentViewCommon.hide()

            val data = _object as List<GetAppointmentsData?>
            if (data.isNullOrEmpty()) {
                homeFragmentBinding.todaysAppointmentViewCommon.showNoData()
            }
            bindTodaysAppointment(data)
            lastUpdatedText(timestamp)
        } else if (callFrom == "dashboard") {

            homeFragmentBinding.dashboardTicketsProgressBar.stop()
            bindUI(_object as List<GetDashboardData?>)
            lastUpdatedText(timestamp)
        } else if (callFrom == "accept_tkt") {
            val data = _object as List<AcceptRejectTicket?>
            showSuccessMessage(data[0]?.StatusMessage ?: "Ticket Accepted Successfully!")
        } else if (callFrom == "ticket_details") {
            val data = _object as List<GetTicketDetailsData?>

            if (!data.isNullOrEmpty()) {
                if (data[0]?.IsCheckedIn == 0) {
                    val intent = Intent(requireContext(), CheckInActivity::class.java)
                    intent.putExtra(ARG_PARAM, data[0])
                    startActivityForResult(intent, REFRESH_REQUEST_CODE)
                    //   CheckInActivity.start(requireContext(), item = data[0])
                } else {
                    val intent = Intent(requireContext(), TicketInfoActivity::class.java)
                    intent.putExtra(ARG_PARAM, data[0])
                    startActivityForResult(intent, REFRESH_REQUEST_CODE)
                    //TicketInfoActivity.start(requireContext(), data[0])
                }
            }
        }
    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {


        if (callFrom == "tickets_cnt") {

            if (isNetworkError) {
                homeFragmentBinding.serviceTicketsViewCommon.showNoInternet()
            } else {
                homeFragmentBinding.serviceTicketsViewCommon.showNoData()
            }
            bindTicketsCount(ArrayList())
        }

        if (callFrom == "all_assigned_tickets") {
            homeFragmentBinding.assignedTicketsProgressBar.stop()

            val data: MutableList<GetAllAssignedTicketsData?> = ArrayList()

//            val noData = GetAllAssignedTicketsData()
//            noData.ViewType = GlobalConstant.TYPE_NO_DATA
//            data.add(noData)

            setupCustomIndicator(data)
        }
        if (callFrom == "dashboard") {
            homeFragmentBinding.dashboardTicketsProgressBar.stop()
        }

        if (callFrom == "today_appointment") {
            if (isNetworkError) {
                homeFragmentBinding.todaysAppointmentViewCommon.showNoInternet()
            } else {
                homeFragmentBinding.todaysAppointmentViewCommon.showNoData()
            }
            bindTodaysAppointment(ArrayList())
        }
    }

    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


    private fun bindUI(list: List<GetDashboardData?>?) = Coroutines.main {
        list?.let {
            try {
                homeFragmentBinding.tvInProgressTicket.text = it[0]?.InProgressTicket.toString()
                homeFragmentBinding.tvAllTicket.text = it[0]?.AllTicket.toString()
                homeFragmentBinding.tvPendingTicket.text = it[0]?.PendingTicket.toString()
                homeFragmentBinding.tvUrgentTicket.text = it[0]?.UrgentTicket.toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun showSuccessMessage(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(message)
            .setNeutralButton(resources.getString(R.string.str_ok)) { dialog, which ->
                viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                    if (user != null) {
                        viewModel.getAllAssignedTickets(user.UserId)
                        viewModel.getDashboardData(userID = user.UserId)

                        viewModel.getTicketsCount(
                            userId = user.UserId,
                            fromDate = DEFAULT_DATE,
                            toDate = DEFAULT_DATE
                        )

                        viewModel.getAppointments(
                            userId = user.UserId,
                            fromDate = strDate,
                            toDate = strDate
                        )
                    }
                })
            }
            .show()
    }

    private fun confirmAcceptTicket(data: GetAllAssignedTicketsData) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ticket No: ${data.Tktno}")
            .setMessage(resources.getString(R.string.supporting_text_accept))
            .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
            .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->

                viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                    if (user != null) {
                        viewModel.acceptTicket(user.UserId, data.TicketID ?: -1,latitude.toString(),longitude.toString(),
                            getAddressFromLatLong(requireContext(), latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road")
                    }
                })
            }
            .show()
    }


    private fun showRejectTicketDialog(modelItem: GetAllAssignedTicketsData) {

        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Ticket No: ${modelItem.Tktno}")
                .setMessage(resources.getString(R.string.supporting_text_reject))
                .setNegativeButton(resources.getString(R.string.str_cancel)) { dialog, which -> }
                .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->

                    val dialogFragment = TicketUnacceptanceDialog.newInstance()
                    dialogFragment.callBack = this
                    val bundle = Bundle()
                    bundle.putInt("ticketNo", modelItem.TicketID ?: -1)
                    bundle.putInt("type", 1)
                    bundle.putString("deviceLatitude",latitude.toString())
                    bundle.putString("deviceLongitude",longitude.toString())
                    bundle.putString("deviceAddress",getAddressFromLatLong(requireContext(), latitude ?: 0.0, longitude ?: 0.0) ?: "Unnamed Road")
                    dialogFragment.arguments = bundle
                    val ft = requireActivity().supportFragmentManager.beginTransaction()
                    val prev = requireActivity().supportFragmentManager.findFragmentByTag("reject_tkt_dialog")
                    if (prev != null) {
                        ft.remove(prev)
                    }
                    ft.addToBackStack(null)
                    dialogFragment.show(ft, "reject_tkt_dialog")
                }
                .show()

    }




    override fun onAcceptTicket(data: GetAllAssignedTicketsData) {
        if(latitude!! > 0.0 && longitude!! > 0.0){
            confirmAcceptTicket(data)
        }else{
            Wherebout(requireContext()).onChange(object : Workable<GPSPoint?> {
                override fun work(t: GPSPoint?) {
                    // draw something in the UI with this new data
                    latitude = t?.latitude
                    longitude = t?.longitude
                    confirmAcceptTicket(data)
                }
            })
        }
    }

    override fun onRejectTicket(data: GetAllAssignedTicketsData) {
        if(latitude!! > 0.0 && longitude!! > 0.0){
            showRejectTicketDialog(data)
        }else{
            Wherebout(requireContext()).onChange(object : Workable<GPSPoint?> {
                override fun work(t: GPSPoint?) {
                    // draw something in the UI with this new data
                    latitude = t?.latitude
                    longitude = t?.longitude
                    showRejectTicketDialog(data)
                }
            })

        }

    }


    override fun onRejectTkt() {

        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.getAllAssignedTickets(user.UserId)
                viewModel.getDashboardData(userID = user.UserId)
                viewModel.getTicketsCount(
                    userId = user.UserId,
                    fromDate = DEFAULT_DATE,
                    toDate = DEFAULT_DATE
                )
            }
        })
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


    companion object {

        private const val REFRESH_REQUEST_CODE = 101
        private const val REFRESH_RESULT_CODE = 102
        private const val RC_LOCATION_PERM = 121

    }

}

