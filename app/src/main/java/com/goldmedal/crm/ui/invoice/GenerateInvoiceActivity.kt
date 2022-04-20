package com.goldmedal.crm.ui.invoice

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.AddedInvoiceItemData
import com.goldmedal.crm.data.model.GetItemForInvoiceData
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.goldmedal.crm.databinding.ActivityGenerateInvoiceBinding
import com.goldmedal.crm.ui.ticket.*
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.alertDialog
import com.goldmedal.crm.util.interfaces.IStatusListener
import com.goldmedal.crm.util.interfaces.OnRefreshListener
import com.goldmedal.crm.util.interfaces.OnRemoveInvoiceItemListener
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_generate_invoice.*
import org.json.JSONArray
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import kotlin.math.roundToInt


private const val ARG_PARAM = "model_item"

class GenerateInvoiceActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any>,
    OnRemoveInvoiceItemListener {

    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var binding: ActivityGenerateInvoiceBinding
    private var modelItem: GetTicketDetailsData? = null
    private var addItemData: AddedInvoiceItemData? = null
    private var listAddedItems: MutableList<AddedInvoiceItemData?>? = ArrayList()

    private var itemPrice = 0.0
    private var totalTaxPercent = 0.0
    private var afterDiscountAmnt = 0
    private var preTaxAmount = 0
    private var taxAmount1 = 0
    private var taxPercent1 = 0.0
    private var taxAmount2 = 0
    private var taxPercent2 = 0.0
    private var totalQty = 0
    private var roundoffAmount = 0
    private var amountAfterTax = 0

    private var totalTaxAmount1 = 0
    private var totalTaxAmount2 = 0
    private var totalPreTaxAmount = 0
    private var totalDiscountAmount = 0
    private var totalAfterDiscountAmount = 0

    var userID = 0

    // - - - - If taxType is 1 then taxamount1(50%) and taxamount2(50%) else only taxamount1(100%) and taxamount2 as 0
    private var taxType = 1
    private var selectedItem: GetItemForInvoiceData? = null

    var strSearchBy: String = ""
    private var customerId: Int = -1

    private var scanType: Int = 1
    private var qrCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            modelItem = it.getParcelableExtra(ARG_PARAM)
            scanType = it.getIntExtra("ScanType",1)
            qrCode = it.getStringExtra("QrCode")
            if(qrCode.isEmpty()){
                qrCode = "-"
            }
        }

        if(scanType == 1 && qrCode.equals("-")){
            qrCode = "0"
        }


        binding = ActivityGenerateInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this


        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                userID = user.UserId ?: 0

                customerId = modelItem?.CustomerID ?: -1

                viewModel.getInvoiceItemDetail(
                    strSearchBy,
                    user.UserId ?: 0,
                    customerId,
                    modelItem?.TicketID ?: 0,
                    scanType,
                    qrCode,
                    modelItem?.ProductID ?: 0
                )

                txtCustName.text = modelItem?.CustName ?: "-"
                txtTktNumber.text = modelItem?.TicketNo ?: "-"
                txtAddress.text = modelItem?.CustAddress ?: "-"
                tvContactNo.text = modelItem?.CustContactNo ?: "-"
                tvEmailID.text = modelItem?.EmailID ?: "-"
                tvTktStatus.text = modelItem?.TicketStatus ?: "-"
            }
        })

        tvAddItem.setOnClickListener {

            if (selectedItem != null) {
                if ((listAddedItems?.count() ?: 0) > 0) {
                    var ifItemExist =
                        listAddedItems?.any { it?.ItemID == selectedItem?.Slno } ?: false
                    if (ifItemExist) {
                        toast("${selectedItem?.ItemName ?: "-"} already selected")
                    } else {
                        addItem()
                    }

                } else {
                    addItem()
                }
            } else {
                toast("Please Select a Valid Item")
            }
        }

        tvGenerateInvoice.setOnClickListener {

            val jsArray = Gson().toJson(listAddedItems)
            val jsonArray = JSONArray(jsArray)
            Log.d("Arraduad ___", jsArray)

            if (listAddedItems?.count() == 0) {
                toast("Please Add Items")
            } else {
                viewModel.getLoggedInUser().observe(this, Observer { user ->
                    if (user != null) {
                        viewModel.generateInvoiceForItems(
                            0,
                            modelItem?.CustomerID ?: 0,
                            modelItem?.TicketID ?: 0,
                            modelItem?.TicketStatus ?: "-",
                            taxType,
                            totalTaxAmount1.toDouble(),
                            totalTaxAmount2.toDouble(),
                            totalPreTaxAmount.toDouble(),
                            totalDiscountAmount.toDouble(),
                            totalAfterDiscountAmount.toDouble(),
                            roundoffAmount.toDouble(),
                            user.UserId ?: 0,
                            1,
                            1,
                            jsonArray
                        )
                    }
                })
            }

        }

    }

    private fun addItem() {
        itemPrice = edt_price.text.toString().toDouble()
        totalTaxPercent = edt_tax.text.toString().toDouble()
        totalQty = edt_qty.text.toString().toInt()

        afterDiscountAmnt =
            ((itemPrice.toInt()) - (selectedItem?.DiscountAmt ?: 0))
        preTaxAmount = (itemPrice * totalQty).toDouble().roundToInt()

        taxPercent1 = String.format("%.2f", totalTaxPercent / 2.0).toDouble()
        taxAmount1 = (preTaxAmount * (taxPercent1 / 100.0)).toDouble().roundToInt()

        taxPercent2 = String.format("%.2f", totalTaxPercent / 2.0).toDouble()
        taxAmount2 = (preTaxAmount * (taxPercent2 / 100.0)).toDouble().roundToInt()

        amountAfterTax = (preTaxAmount + taxAmount1 + taxAmount2).toDouble().roundToInt()

        addItemData = AddedInvoiceItemData(
            selectedItem?.ItemName ?: "-",
            0,
            selectedItem?.Slno ?: -1,
            totalQty,
            itemPrice,
            selectedItem?.DiscountPer ?: 0.0,
            selectedItem?.DiscountAmt ?: 0,
            afterDiscountAmnt,
            taxType,
            taxAmount1,
            taxAmount2,
            taxPercent1,
            taxPercent2,
            preTaxAmount,
            amountAfterTax
        )

        // roundoffAmount = edt_roundoff_amount.text.toString().toDouble()

        listAddedItems?.add(addItemData)
        bindUI(listAddedItems)

    }

    private fun initSpinnerItemList(itemList: List<GetItemForInvoiceData?>?) {
        var listItemDetail: List<String>? = null

        listItemDetail = ArrayList()


        itemList.let {
            for (i in 1..((it?.size) ?: 0)) {
                println(i)
                listItemDetail.add((it?.get(i - 1)?.ItemName ?: "NO ITEM"))
            }

            binding.spItemSearch.item = listItemDetail

            binding.spItemSearch.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {
//                        Toast.makeText(
//                            this@GenerateInvoiceActivity,
//                            listItemDetail!![position],
//                            Toast.LENGTH_SHORT
//                        ).show()
                        if (it!![position] != null) {
                            setItemValues(it!![position])
                            selectedItem = it!![position]
                        } else {
                            toast("Invalid Ticket Selected")
                        }
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>) {

                    }
                }
        }

    }

    override fun onRemoveClick(slNo: Int, position: Int) {
        if (position >= 0) {
            listAddedItems?.removeAt(position)
            bindUI(listAddedItems)
        }
    }

    private fun setItemValues(data: GetItemForInvoiceData?) {
        tvItemCode.text = data?.itemCode ?: "-"
        tvItemColor.text = data?.ItemColor ?: "-"
        tvItemcategory.text = data?.Category ?: "-"
        tvItemSubCategory.text = data?.SubCategory ?: "-"

        edt_price.setText((data?.Rate ?: 0.0).toString())
        edt_tax.setText((data?.TaxPer ?: 0.0).toString())
        edt_qty.setText("1")
        edt_roundoff_amount.setText((roundoffAmount).toString())
    }

    private fun List<AddedInvoiceItemData?>.toAddedInvoiceItem(): List<AddedInvoiceItem?> {
        return this.map {
            AddedInvoiceItem(it, this@GenerateInvoiceActivity, this@GenerateInvoiceActivity)
        }
    }


    private fun bindUI(list: List<AddedInvoiceItemData?>?) = Coroutines.main {
        list?.let {
            if (it != null) {

                roundoffAmount = list.map { it?.FinalPrice ?: 0 }.sum()
                totalTaxAmount1 = list.map { it?.TaxAmount1 ?: 0 }.sum()
                totalTaxAmount2 = list.map { it?.TaxAmount2 ?: 0 }.sum()
                totalPreTaxAmount = list.map { it?.PreTaxAmount ?: 0 }.sum()
                totalDiscountAmount = list.map { it?.DiscountAmount ?: 0 }.sum()
                totalAfterDiscountAmount = list.map { it?.AfterDiscountAmount ?: 0 }.sum()

                edt_roundoff_amount.setText(roundoffAmount.toString())
                initRecyclerView(it.toAddedInvoiceItem())
            }

        }


    }

    private fun initRecyclerView(toAddedInvoiceItem: List<AddedInvoiceItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toAddedInvoiceItem)
        }

        binding.rvListInvoice.apply {
            layoutManager = LinearLayoutManager(this@GenerateInvoiceActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }


    override fun onStarted(callFrom: String) {
        // binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        // binding.viewCommon.hide()
        if (callFrom.equals("invoice_item")) {
            val data = _object as List<GetItemForInvoiceData?>

            if (data.count() > 0) {
                initSpinnerItemList(data)
            }
        }

        if (callFrom.equals("invoice_generate")) {
            // binding.rootLayout.snackbar("Invoice generated successfully")
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Invoice generated successfully")

            builder.setPositiveButton(R.string.str_ok, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id12: Int) {
                    finish()
                    refreshListener?.onRefresh()
                }
            })

            val alertDialog = builder.create()
            alertDialog.show()
        }

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

//        binding.viewCommon.hide()
//        if (isNetworkError) {
//            binding.viewCommon.showNoInternet()
//        } else {
//            binding.viewCommon.showServerError()
//        }
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

        var refreshListener: OnRefreshListener? = null

        fun start(context: Context) {
            context.startActivity(Intent(context, GenerateInvoiceActivity::class.java))
        }
    }


}