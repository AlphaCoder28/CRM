package com.goldmedal.crm.ui.dashboard.leftpanel

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.data.model.GetTicketDetailsForEngineerData
import com.goldmedal.crm.data.model.GetTicketsCountData
import com.goldmedal.crm.databinding.ActivityProgressReportBinding
import com.goldmedal.crm.ui.dashboard.home.HomeViewModel
import com.goldmedal.crm.ui.dashboard.home.HomeViewModelFactory
import com.goldmedal.crm.ui.ticket.ServiceTicketItem
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.interfaces.OnCardClickListener
import com.goldmedal.crm.util.interfaces.OnServiceTicketClickListener
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toString
import com.google.android.material.tabs.TabLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.ArrayList

class ProgressReportActivity : AppCompatActivity(), KodeinAware, DashboardApiListener<Any>,
     OnCardClickListener ,OnServiceTicketClickListener,FilterDatesBottomSheetFragment.ItemClickListener{

    override val kodein by kodein()
    private val factory: HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityProgressReportBinding

    private var statusBy: Int = 1

    private var selectedTimeFilter = "last_3_months"

    private var fromDate = ""
    private var toDate = ""
    var strSearchBy: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProgressReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.apiListener = this



        binding.textViewFilter.text = "Last 3 months"

       val startCalendar = Calendar.getInstance()
        startCalendar.add(Calendar.MONTH, -3)
        val endCalendar = Calendar.getInstance()
        val previous = startCalendar.time
        val today = endCalendar.time
        fromDate = previous.toString("MM-dd-yyyy")
        toDate = today.toString("MM-dd-yyyy")

        Log.d("TAG", "onCreate: fromDate"+ fromDate + "toDate: "+ toDate)

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getTicketsCount(user.UserId,
                    fromDate,
                    toDate)
            }
        })



        binding.btnChange.setOnClickListener {
            supportFragmentManager.let {
                FilterDatesBottomSheetFragment.newInstance(selectedTimeFilter).apply {
                    show(it, tag)
                }
            }

        }
    }



    private fun initTabs(list: List<GetTicketsCountData?>) {
        list.let {

            binding.tabs.removeAllTabs()

            for (elements in list) {
                binding.tabs.addTab(binding.tabs.newTab()
                    .setText(elements?.TicketName)
                    .setId(elements?.TicketId ?: 0)
                )
            }

            viewModel.getLoggedInUser().observe(this, Observer { user ->
                if (user != null) {
                    viewModel.getTicketDetailsForEngineer(
                        userId = user.UserId,
                        fromDate = fromDate,
                        toDate = toDate,
                        statusBy = statusBy,
                        searchBy = strSearchBy
                    )
                }
            })
        }



        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

               statusBy = tab.id
                binding.searchView.setQuery("", false);
                binding.searchView.clearFocus();
                strSearchBy = ""

                viewModel.getLoggedInUser().observe(this@ProgressReportActivity, Observer { user ->
                    if (user != null) {
                        viewModel.getTicketDetailsForEngineer(
                            userId = user.UserId,
                            fromDate = fromDate,
                            toDate = toDate,
                            statusBy = statusBy,
                            searchBy = strSearchBy
                        )
                    }
                })
//                if (tab.position == 0) {
//                if (etSearch.text.toString().isNotEmpty()) {
//                    etSearch.setText("")
//                } else {
//                    filterArrays(tabPosition = tab.position)
//                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        // - - - - searchview - - - - - -
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                            .observe(this@ProgressReportActivity, Observer { user ->

                                if (user != null) {
                                    viewModel.getTicketDetailsForEngineer(
                                            userId = user.UserId,
                                            fromDate = fromDate,
                                            toDate = toDate,
                                            statusBy = statusBy,
                                            searchBy = strSearchBy
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
                            userId = user.UserId,
                            fromDate = fromDate,
                            toDate = toDate,
                            statusBy = statusBy,
                            searchBy = strSearchBy
                    )
                }
            })
            false
        }




    }


    private fun List<GetTicketsCountData?>.toData(): List<FancyItem?> {
        return this.map {
            FancyItem(it, this@ProgressReportActivity,this@ProgressReportActivity)
        }
    }

    private fun initGridRecyclerView(data: List<FancyItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }

        binding.rvList.apply {
            layoutManager = GridLayoutManager(context,3)
            setHasFixedSize(true)
//            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<GetTicketsCountData?>?) = Coroutines.main {
        list?.let {
            initGridRecyclerView(it.toData())
        }
    }


    private fun List<GetTicketDetailsForEngineerData?>.serviceTicketsData(): List<ServiceTicketItem?> {
        return this.map {
            ServiceTicketItem(it, this@ProgressReportActivity, this@ProgressReportActivity,null,
                statusBy
            )
        }
    }

    private fun initRecyclerView(serviceTicketsData: List<ServiceTicketItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(serviceTicketsData)
        }

        binding.rvTicketDetails.apply {
            layoutManager = LinearLayoutManager(this@ProgressReportActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun bindRecyclerview(list: List<GetTicketDetailsForEngineerData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.serviceTicketsData())
        }
    }








    override fun onStarted(callFrom: String) {

        if (callFrom == "tickets_cnt") {
            binding.progressBar.start()
        }else if (callFrom == "service_tickets") {
           binding.ticketDetailsViewCommon.showProgressBar()
        }
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String, timestamp: String) {



        if (callFrom == "tickets_cnt") {
            binding.progressBar.stop()
            bindUI(_object as List<GetTicketsCountData?>)
            initTabs(_object)
        }else if (callFrom == "service_tickets") {
            binding.ticketDetailsViewCommon.hide()
            bindRecyclerview(_object as List<GetTicketDetailsForEngineerData?>)
        }


    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        if (callFrom == "tickets_cnt") {
            binding.progressBar.stop()
//            if (isNetworkError) {
//                binding.viewCommon.showNoInternet()
//            } else {
//                binding.viewCommon.showNoData()
//            }
            bindUI(ArrayList())


        }else if (callFrom == "service_tickets") {
            binding.ticketDetailsViewCommon.hide()
            if (isNetworkError) {
                binding.ticketDetailsViewCommon.showNoInternet()
            } else {
                binding.ticketDetailsViewCommon.showNoData()
            }
            bindRecyclerview(ArrayList())
        }

        binding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onTicketClick(model: GetTicketDetailsForEngineerData?, actionId: Int) {

        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getTicketDetails(user.UserId, model?.TicketID ?: -1)
            }
        })

    }


    override fun onCardClick(position: Int, ticketId: Int) {
        statusBy = ticketId
        binding.tabs.getTabAt(position)?.select()
    }


    override fun onItemClick(fromDate: String, toDate: String, param: String, selectedText: String) {

        Log.d("TAG", "onItemClick: fromDate: "+fromDate + " toDate:" + toDate)

        selectedTimeFilter = param
        this.fromDate = fromDate
        this.toDate = toDate

        binding.textViewFilter.text = selectedText

        viewModel.getLoggedInUser().observe(this@ProgressReportActivity, Observer { user ->
            if (user != null) {

                viewModel.getTicketsCount(user.UserId,
                    fromDate,
                    toDate)
                viewModel.getTicketDetailsForEngineer(
                    userId = user.UserId,
                    fromDate = fromDate,
                    toDate = toDate,
                    statusBy = statusBy,
                    searchBy = "-"
                )
            }
        })



//        when (param) {
////            "scan" -> {
////                QrCodeScanActivity.start(requireContext(), this)
////            }
////            "search" -> {
////                searchQRCode()
////            }
//            else -> {
//                // (Nanimoshinai)
//            }
//        }
    }
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ProgressReportActivity::class.java))
        }
    }

}