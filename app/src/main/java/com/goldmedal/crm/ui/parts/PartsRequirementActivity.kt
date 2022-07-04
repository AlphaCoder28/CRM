package com.goldmedal.crm.ui.parts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.*
import com.goldmedal.crm.databinding.PartsRequirementBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.interfaces.OnRemoveInvoiceItemListener
import com.goldmedal.crm.util.interfaces.PartRequirementClickListener
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toast
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.add_parts_requirement.*
import org.json.JSONArray
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class PartsRequirementActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any>,
    OnRemoveInvoiceItemListener, PartRequirementClickListener {

    override val kodein by kodein()

    private val factory: PartsViewModelFactory by instance()

    private lateinit var viewModel: PartsViewModel

    private lateinit var binding: PartsRequirementBinding

    var tabPosition = 0

    private var selectedItem: SelectPartsListData? = null
    private var listAddedParts: MutableList<AddedPartsData?>? = ArrayList()
    private var addPartsData: AddedPartsData? = null

    private var totalQty = 0

    private var userID = 0
    private var ticketID = 0
    private var custID = 0
    private var modelItem: GetTicketDetailsData? = null
    private lateinit var callFromScreen: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callFromScreen = intent.getStringExtra("CallFrom").toString()
        modelItem = intent.getParcelableExtra("TicketDetail")

        //  toast("callfrom - - - - "+callFrom)

        binding = PartsRequirementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        if (callFromScreen.equals("Ticket")) {
            resetBindUI()

            custID = modelItem?.CustomerID ?: 0
            ticketID = modelItem?.TicketID ?: 0

            binding.layoutAddParts.txtPartsReqCustName.text = modelItem?.CustName ?: "-"
            binding.layoutAddParts.txtPartsReqTicketNumber.text = modelItem?.TicketNo ?: "-"

            binding.layoutAddParts.txtPartsReqCustName.visibility = View.VISIBLE
            binding.layoutAddParts.txtPartsReqTicketNumber.visibility = View.VISIBLE

            binding.layoutAddParts.spTktNumber.visibility = View.GONE
            binding.layoutAddParts.spCustName.visibility = View.GONE

            binding.layoutAddParts.txtContactNumber.text = modelItem?.CustContactNo ?: "-"
            binding.layoutAddParts.txtAddress.text = modelItem?.CustAddress ?: "-"
            binding.layoutAddParts.txtTktStatus.text = modelItem?.TicketStatus ?: "-"
        } else {
            binding.layoutAddParts.txtPartsReqCustName.visibility = View.GONE
            binding.layoutAddParts.txtPartsReqTicketNumber.visibility = View.GONE

            binding.layoutAddParts.spTktNumber.visibility = View.VISIBLE
            binding.layoutAddParts.spCustName.visibility = View.VISIBLE
        }

        initTabs()

        llPartsAddedMain.visibility = View.GONE

        viewModel = ViewModelProvider(this, factory).get(PartsViewModel::class.java)
        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                userID = user.UserId ?: 0
                // - - - This will get all customers list - - - -
                if (callFromScreen.equals("Dashboard")) {
                    viewModel.getPartsRequirementDetail(userID, 0, 0)
                }

                viewModel.getStockPartsList(userID, "0", "")
            }
        })


        tvAddItem.setOnClickListener {

            if (userID == 0) {
                toast("Please select valid User")
            } else if (custID == 0) {
                toast("Please select valid Customer")
            } else if (ticketID == 0) {
                toast("Please select valid Ticket")
            } else {
                if (edtPartsQty.text.isNullOrEmpty() || (edtPartsQty.text.toString()
                        .toInt() == 0)
                ) {
                    toast("Select valid Quantity")
                } else {
                    if (selectedItem != null) {
                        if ((listAddedParts?.count() ?: 0) > 0) {
                            var ifItemExist =
                                listAddedParts?.any { it?.PartID == selectedItem?.PartId } ?: false
                            if (ifItemExist) {
                                toast("${selectedItem?.PartName ?: "-"} already selected")
                            } else {
                                addParts()
                            }
                        } else {
                            addParts()
                        }
                    } else {
                        toast("Please Select a Valid Part")
                    }
                }

            }
        }


        tvSubmit.setOnClickListener {
            val jsArray = Gson().toJson(listAddedParts)
            val jsonArray = JSONArray(jsArray)
            Log.d("Array for parts req ___", jsArray)

            if (listAddedParts?.count() == 0) {
                toast("Please Add Items")
            } else {
                viewModel.getLoggedInUser().observe(this, Observer { user ->
                    if (user != null) {
                        viewModel.submitPartsRequirement(
                            userID,
                            custID,
                            ticketID,
                            jsonArray
                        )
                    }
                })
            }

        }

    }


    private fun addParts() {

        totalQty = edtPartsQty.text.toString().toInt()

        addPartsData = AddedPartsData(
            selectedItem?.PartName ?: "-",
            selectedItem?.PartId ?: 0,
            totalQty
        )

        listAddedParts?.add(addPartsData)

        listAddedParts?.let {
            if (it != null) {
                initAddPartRecyclerView(it.toAddPartsData())
            }
        }
        //binding.layoutAddParts.rvListAddParts.adapter?.notifyDataSetChanged()

    }


    private fun initTabs() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                tabPosition = tab.position
                if (tabPosition == 0) {
                    binding.layoutAddParts.root.visibility = View.VISIBLE
                    binding.layoutListParts.root.visibility = View.INVISIBLE
                } else {

                    viewModel.getPartsRequirementList(userID)

                    binding.layoutAddParts.root.visibility = View.INVISIBLE
                    binding.layoutListParts.root.visibility = View.VISIBLE

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    // - - - -  - Code to add list of parts requirement in recycler view - - - -  - --
    private fun List<GetRequestPartListData?>.toPartsRequirementList(): List<PartsRequirementListItem?> {
        return this.map {
            PartsRequirementListItem(
                it,
                this@PartsRequirementActivity,
                this@PartsRequirementActivity
            )
        }
    }


    private fun bindPartsListUI(list: List<GetRequestPartListData?>?) = Coroutines.main {
        list?.let {
            initPartsReqListRecyclerView(it.toPartsRequirementList())
        }
    }

    private fun initPartsReqListRecyclerView(toPartsRequirementList: List<PartsRequirementListItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toPartsRequirementList)
        }

        binding.layoutListParts.rvListParts.apply {
            layoutManager = LinearLayoutManager(this@PartsRequirementActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun itemClicked(reqNo: String?, callFrom: String?) {
        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                userID = user.UserId ?: 0

                val dialogFragment = PartsReqItemPopup.newInstance()
                val bundle = Bundle()
                bundle.putString("CallFrom", callFrom ?: "")
                bundle.putString("RequestNo", reqNo ?: "-")
                dialogFragment.arguments = bundle
                val ft = this.supportFragmentManager.beginTransaction()
                val prev = this.supportFragmentManager.findFragmentByTag("parts_req_item_dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                dialogFragment.show(ft, "parts_req_item_dialog")
            }
        })

    }


    //  - - - - - code for locally adding parts - - - - - --
    private fun List<AddedPartsData?>.toAddPartsData(): List<AddedPartsItem?> {
        return this.map {
            AddedPartsItem(it, this@PartsRequirementActivity, this@PartsRequirementActivity)
        }
    }


    private fun initAddPartRecyclerView(toAddPartsData: List<AddedPartsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toAddPartsData)
        }

        binding.layoutAddParts.rvListAddParts.apply {
            layoutManager = LinearLayoutManager(this@PartsRequirementActivity)
            // setHasFixedSize(true)
            adapter = mAdapter
        }

        if (toAddPartsData.count() > 0) {
            llPartsAddedMain.visibility = View.VISIBLE
        } else {
            llPartsAddedMain.visibility = View.GONE
        }
    }


    // - - - - -  -  code for locally deleting parts from local part list - - - - - -
    override fun onRemoveClick(slNo: Int, position: Int) {
        if (position >= 0) {
            listAddedParts?.removeAt(position)
            listAddedParts?.let {
                if (it != null) {
                    initAddPartRecyclerView(it.toAddPartsData())
                }
            }
            // binding.layoutAddParts.rvListAddParts.adapter?.notifyDataSetChanged()
        }
    }

    override fun onStarted(callFrom: String) {
        if (callFrom.equals("PartsCustomerList") || callFrom.equals("PartsTicketNoList") || callFrom.equals(
                "PartsAllDetails"
            )
        ) {
            resetBindUI()
        }

        if (callFrom.equals("parts_requirement_list")) {
            binding.layoutListParts.viewCommon.showProgressBar()
        }
    }


    // - - - - - - when API yields success response ---- ------- --------
    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        if (callFrom.equals("PartsCustomerList")) {
            val data = _object as List<GetPartsRequirementData?>

            if (data.count() > 0) {
                initSpinnerCustomerList(data)
            }
        }

        if (callFrom.equals("PartsTicketNoList")) {
            val data = _object as List<GetPartsRequirementData?>

            if (data.count() > 0) {
                initSpinnerTicketList(data)
            }
        }

        if (callFrom.equals("PartsAllDetails")) {
            val data = _object as List<GetPartsRequirementData?>

            if (data.count() > 0) {
                bindUI(data)
            }
        }

        if (callFrom.equals("StockPartsList")) {
            val data = _object as List<SelectPartsListData?>

            if (data.count() > 0) {
                initSpinnerPartsList(data)
            }
        }

        if (callFrom.equals("submit_parts_requirement")) {
            val data = _object as List<SubmitPartsReqData?>
            if (data.count() > 0) {
                toast("Parts Request Submitted Successfully")

                resetBindUI()

                viewModel.getPartsRequirementList(userID)

                binding.layoutAddParts.root.visibility = View.INVISIBLE
                binding.layoutListParts.root.visibility = View.VISIBLE

                binding.tabs.getTabAt(1)?.select();

                binding.layoutAddParts.spTktNumber.clearSelection()
                binding.layoutAddParts.spCustName.clearSelection()

                ticketID = 0
                custID = 0
            }
        }

        if (callFrom.equals("parts_requirement_list")) {
            binding.layoutListParts.viewCommon.hide()
            val data = _object as List<GetRequestPartListData?>
            if (data.count() > 0) {
                bindPartsListUI(data)
            } else {
                binding.layoutListParts.viewCommon.showNoData()
            }
        }

    }

    // - - - - - set customer list inside spinner  --  - -- -
    private fun initSpinnerCustomerList(itemList: List<GetPartsRequirementData?>?) {

        var listCustomers: List<String>? = null

        listCustomers = ArrayList()

        itemList.let {
            for (i in 1..((it?.size) ?: 0)) {
                println(i)
                listCustomers.add((it?.get(i - 1)?.CustName ?: "NO CONTACTS"))
            }
            binding.layoutAddParts.spCustName.item = listCustomers

            binding.layoutAddParts.spCustName.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(
                            this@PartsRequirementActivity,
                            listCustomers[position],
                            Toast.LENGTH_SHORT
                        ).show()
                        if (it?.get(position) != null) {
                            custID = it[position]?.CustID ?: 0
                            // - - - - - call API to get Ticket List - - - -
                            if (custID != 0) {
                                viewModel.getPartsRequirementDetail(userID, custID, 0)
                            } else {
                                toast("Invalid Customer")
                            }
                        } else {
                            toast("Invalid Customer Selected")
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {

                    }
                }
        }

    }

    // - - - -- - -  set ticket no list inside spinner  - - - - - - -
    private fun initSpinnerTicketList(itemList: List<GetPartsRequirementData?>?) {

        var listTickets: List<String>? = null

        listTickets = ArrayList()


        itemList.let {
            for (i in 1..((it?.size) ?: 0)) {
                println(i)
                listTickets.add((it?.get(i - 1)?.TicketNo ?: "NO TICKETS"))
            }
            binding.layoutAddParts.spTktNumber.item = listTickets

            binding.layoutAddParts.spTktNumber.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(
                            this@PartsRequirementActivity,
                            listTickets[position],
                            Toast.LENGTH_SHORT
                        ).show()
                        if (it?.get(position) != null) {
                            ticketID = it[position]?.TicketID ?: 0
                            // - - - - - call API to get details - - - -
                            if (ticketID != 0) {
                                viewModel.getPartsRequirementDetail(
                                    0,
                                    0,
                                    ticketID
                                )
                            } else {
                                toast("Invalid Ticket No")
                            }
                        } else {
                            toast("Invalid Ticket Selected")
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {

                    }
                }
        }

    }

    private fun bindUI(partsData: List<GetPartsRequirementData?>?) {
        if (partsData.isNullOrEmpty()) {
            resetBindUI()
        } else {
            binding.layoutAddParts.txtContactNumber.text = partsData?.get(0)?.ContactNo ?: "-"
            binding.layoutAddParts.txtAddress.text = partsData?.get(0)?.Address ?: "-"
            binding.layoutAddParts.txtTktStatus.text = partsData?.get(0)?.TktStatus ?: "-"
        }

    }

    // - - - - - - clear all UI when selection changes - -  - - - --
    private fun resetBindUI() {

        binding.layoutAddParts.txtContactNumber.text = "-"
        binding.layoutAddParts.txtAddress.text = "-"
        binding.layoutAddParts.txtTktStatus.text = "-"

        totalQty = 0
        edtPartsQty.clearFocus()
        edtPartsQty.setText("")

        listAddedParts?.clear()

        listAddedParts?.let {
            if (it != null) {
                initAddPartRecyclerView(it.toAddPartsData())
            }
        }

        binding.layoutAddParts.spSelectPart.clearSelection()
        //  binding.layoutAddParts.rvListAddParts.adapter?.notifyDataSetChanged()

    }


    // - - - - - - set parts selection list inside spinner - - - - - -
    private fun initSpinnerPartsList(itemList: List<SelectPartsListData?>?) {
        var listParts: List<String>? = null

        listParts = ArrayList()


        itemList.let {
            for (i in 1..((it?.size) ?: 0)) {
                println(i)
                listParts.add((it?.get(i - 1)?.PartName ?: "NO PARTS"))
            }
            binding.layoutAddParts.spSelectPart.item = listParts

            binding.layoutAddParts.spSelectPart.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
                        Toast.makeText(
                            this@PartsRequirementActivity,
                            listParts!![position],
                            Toast.LENGTH_SHORT
                        ).show()
                        if (it!![position] != null) {
//                            setItemValues(it!![position])
//                            selectedItem = it!![position]
                            // - - - - - call API to get Ticket List - - - -
                            if (it != null && it[position]?.PartId != 0) {
                                selectedItem = it!![position]
                            } else {
                                toast("Invalid Part")
                            }
                        } else {
                            toast("Invalid Part Selected")
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {

                    }
                }
        }

    }


    // - - - -  Error respone of API - - - - - - -
    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (callFrom.equals("parts_requirement_list")) {
            binding.layoutListParts.viewCommon.hide()
            if (isNetworkError) {
                binding.layoutListParts.viewCommon.showNoInternet()
            } else {
                binding.layoutListParts.viewCommon.showServerError()
            }
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


    companion object {
        fun start(context: Context, callFrom: String, modelData: GetTicketDetailsData) {
            val intent = Intent(context, PartsRequirementActivity::class.java)
            intent.putExtra("CallFrom", callFrom)
            intent.putExtra("TicketDetail", modelData)
            context.startActivity(intent)
        }
    }


}


