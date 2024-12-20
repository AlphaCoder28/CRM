package com.goldmedal.crm.ui.invoice

// This code provides the logic for generating invoices in an Android app:

// 1. **Setup**: Initializes the UI, retrieves data from the Intent, and configures the back button.
// 2. **ViewModel**: Sets up `TicketViewModel` to handle data and API calls, observing the logged-in user to load invoice details.
// 3. **Payment Method Spinner**: Populates a dropdown for payment methods (Cash, Online) and stores the selected method.
// 4. **Adding Items**: Validates selected items, calculates prices, taxes, and discounts, then adds them to the invoice list.
// 5. **Generating Invoice**: Prepares data (customer, items, taxes, payment method) and sends it to the backend for invoice generation.
// 6. **Item Selection**: Allows item selection from a spinner, displaying details like price, quantity, and tax.
// 7. **Removing Items**: Enables item removal from the invoice list and updates the UI.
// 8. **Binding Data**: Updates UI components (e.g., total amounts, item list) and initializes the RecyclerView with invoice items.
// 9. **API Handling**: Processes responses to fetch items or confirm invoice generation, showing success messages.
// 10. **Error Handling**: Displays error messages via snackbars for network or validation issues.
// 11. **Navigation**: Supports back navigation to the previous screen.
// **Companion Object**: Provides helper functions for launching the activity and refreshing data.


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.widget.AdapterView
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
import com.goldmedal.crm.data.model.PaymentMethodData
import com.goldmedal.crm.data.network.responses.GetItemForInvoiceResponse
import com.goldmedal.crm.databinding.ActivityGenerateInvoiceBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.interfaces.OnRefreshListener
import com.goldmedal.crm.util.interfaces.OnRemoveInvoiceItemListener
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toast
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.angmarch.views.OnSpinnerItemSelectedListener
import org.angmarch.views.SpinnerTextFormatter
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
    private val paymentMethod: MutableList<PaymentMethodData> = java.util.ArrayList()
    private var strPaymentMethod = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // Existing code to handle intent, initialize viewModel, and other setup
        intent?.let {
            modelItem = it.getParcelableExtra(ARG_PARAM)
            scanType = it.getIntExtra("ScanType", 1)
            qrCode = it.getStringExtra("QrCode").toString()
            if (qrCode.isEmpty()) {
                qrCode = "-"
            }
        }

        if (scanType == 1 && qrCode.equals("-")) {
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

                binding.txtCustName.text = modelItem?.CustName ?: "-"
                binding.txtTktNumber.text = modelItem?.TicketNo ?: "-"
                binding.txtAddress.text = modelItem?.CustAddress ?: "-"
                binding.tvContactNo.text = modelItem?.CustContactNo ?: "-"
                binding.tvEmailID.text = modelItem?.EmailID ?: "-"
                binding.tvTktStatus.text = modelItem?.TicketStatus ?: "-"
                initPaymentMethodSpinner()
            }
        })

        // Set click listener for the "Add Item" button
        binding.tvAddItem.setOnClickListener {
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


        // Set click listener for the "Generate Invoice" button
        /* binding.tvGenerateInvoice.setOnClickListener {
            val jsArray = Gson().toJson(listAddedItems)
            val jsonArray = JSONArray(jsArray)
            Log.d("Arraduad ___", jsArray)

            if (listAddedItems?.isEmpty() == true) {
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
                            jsonArray,
                            strPaymentMethod,
                            binding.edtGstNo.text.toString()
                        )
                    }
                })
            }
        }
    }*/


/*// Set click listener for the "Generate Invoice" button
        binding.tvGenerateInvoice.setOnClickListener {
            val jsArray = Gson().toJson(listAddedItems)
            val jsonArray = JSONArray(jsArray)
            Log.d("Arraduad ___", jsArray)

            // Log to track when the button is clicked
            Log.d("GenerateInvoiceButton", "Button clicked - Disabling button")

            // Disable the "Generate Invoice" TextView (Make it unclickable)
            binding.tvGenerateInvoice.isClickable = false
            binding.tvGenerateInvoice.alpha = 0.5f // Reduce alpha to indicate disabled state

            // Log the current state of the button
            Log.d("GenerateInvoiceButton", "Button disabled: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")

            if (listAddedItems?.isEmpty() == true) {
                toast("Please Add Items")
                // Re-enable the button if no items are added
                binding.tvGenerateInvoice.isClickable = true
                binding.tvGenerateInvoice.alpha = 1.0f

                // Log when the button is re-enabled
                Log.d("GenerateInvoiceButton", "No items added - Re-enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")
            } else {
                // Observe the logged-in user to call the API
                viewModel.getLoggedInUser().observe(this, Observer { user ->
                    if (user != null) {
                        // Call the getInvoiceItemDetail API
                        viewModel.getInvoiceItemDetail(
                            strSearchBy,
                            user.UserId ?: 0,
                            customerId,
                            modelItem?.TicketID ?: 0,
                            scanType,
                            qrCode,
                            modelItem?.ProductID ?: 0
                        )

                        // Observe the invoiceItemResponse LiveData to get the response
                        viewModel.invoiceItemResponse.observe(this, Observer { response ->
                            // Re-enable the button
                            binding.tvGenerateInvoice.isClickable = true
                            binding.tvGenerateInvoice.alpha = 1.0f // Make the button fully visible again

                            // Log when the button is re-enabled after response
                            Log.d("GenerateInvoiceButton", "Response received - Re-enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")

                            if (response != null) {
                                // Successfully received the invoice item details
                                if (response.StatusCode == 200) {
                                    // Process the response data
                                    toast("Invoice generated successfully")
                                    // You can also use a dialog to show a success message
                                    AlertDialog.Builder(this)
                                        .setTitle("Invoice Generated")
                                        .setMessage("Your invoice has been generated successfully.")
                                        .setPositiveButton("OK", null)
                                        .show()
                                } else {
                                    // Handle error response if needed
                                    toast("Error: ${response.StatusCodeMessage}")
                                }
                            } else {
                                // Handle null response
                                toast("Error: No data received")
                            }
                        })
                    } else {
                        // Handle the case when user is null (logged out)
                        toast("User not logged in")
                        // Optionally re-enable the button here as well
                        binding.tvGenerateInvoice.isClickable = true
                        binding.tvGenerateInvoice.alpha = 1.0f

                        // Log when the button is re-enabled after user is null
                        Log.d("GenerateInvoiceButton", "User is null - Re-enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")
                    }
                })
            }
        }*/

       /* binding.tvGenerateInvoice.setOnClickListener {
            val jsArray = Gson().toJson(listAddedItems)
            val jsonArray = JSONArray(jsArray)
            Log.d("Arraduad ___", jsArray)

            // Log to track when the button is clicked
            Log.d("GenerateInvoiceButton", "Button clicked - Disabling button")

            // Disable the "Generate Invoice" TextView (Make it unclickable)
            binding.tvGenerateInvoice.isClickable = false
            binding.tvGenerateInvoice.alpha = 0.5f // Reduce alpha to indicate disabled state

            // Log the current state of the button
            Log.d("GenerateInvoiceButton", "Button disabled: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")

            if (listAddedItems?.isEmpty() == true) {
                // Show dialog if no items are added
                AlertDialog.Builder(this)
                    .setTitle("Add Items")
                    .setMessage("Please add items to generate the invoice.")
                    .setPositiveButton("OK", null)
                    .show()

                // Re-enable the button
                binding.tvGenerateInvoice.isClickable = true
                binding.tvGenerateInvoice.alpha = 1.0f

                // Log when the button is re-enabled
                Log.d("GenerateInvoiceButton", "No items added - Re-enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")
            } else {
                // Observe the logged-in user to call the API
                viewModel.getLoggedInUser().observe(this, Observer { user ->
                    if (user != null) {
                        // Call the getInvoiceItemDetail API
                        viewModel.getInvoiceItemDetail(
                            strSearchBy,
                            user.UserId ?: 0,
                            customerId,
                            modelItem?.TicketID ?: 0,
                            scanType,
                            qrCode,
                            modelItem?.ProductID ?: 0
                        )

                        // Observe the invoiceItemResponse LiveData to get the response
                        viewModel.invoiceItemResponse.observe(this, Observer { response ->
                            // Re-enable the button
                            binding.tvGenerateInvoice.isClickable = true
                  *//*
                  binding.tvGenerateInvoice.alpha = 1.0f // Make the button fully visible again*/
        /*

                            // Log when the button is re-enabled after response
                            Log.d("GenerateInvoiceButton", "Response received - Re-enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")

                            if (response != null) {
                                // Successfully received the invoice item details
                                if (response.StatusCode == 200) {
                                    // Show dialog on success
                                    AlertDialog.Builder(this)
                                        .setTitle("Invoice Generated")
                                        .setMessage("Your invoice has been generated successfully.")
                                        .setPositiveButton("OK", null)
                                        .show()
                                } else {
                                    // Handle error response if needed
                                    AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Error: ${response.StatusCodeMessage}")
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            } else {
                                // Handle null response
                                AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Error: No data received")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        })
                    } else {
                        // Handle the case when user is null (logged out)
                        AlertDialog.Builder(this)
                            .setTitle("User Not Logged In")
                            .setMessage("You are not logged in. Please log in to continue.")
                            .setPositiveButton("OK", null)
                            .show()

                        // Re-enable the button
                        binding.tvGenerateInvoice.isClickable = true
                        *//*binding.tvGenerateInvoice.alpha = 1.0f*//*

                        // Log when the button is re-enabled after user is null
                        Log.d("GenerateInvoiceButton", "User is null - Re-enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}, alpha = ${binding.tvGenerateInvoice.alpha}")
                    }
                })
            }
        }*/

        binding.tvGenerateInvoice.setOnClickListener {
            val jsArray = Gson().toJson(listAddedItems)
            val jsonArray = JSONArray(jsArray)
            Log.d("Arraduad ___", jsArray)

            // Log to track when the button is clicked
            Log.d("GenerateInvoiceButton", "Button clicked - Disabling button")

            // Disable the "Generate Invoice" TextView (Make it unclickable)
            binding.tvGenerateInvoice.isClickable = false

            // Log the current state of the button
            Log.d("GenerateInvoiceButton", "Button disabled: isClickable = ${binding.tvGenerateInvoice.isClickable}")

            if (listAddedItems?.isEmpty() == true) {
                toast("Please Add Items")

                // Re-enable the button
                binding.tvGenerateInvoice.isClickable = true

                // Log when the button is re-enabled
                Log.d("GenerateInvoiceButton", "No items added - enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}")
            } else {
                // Observe the logged-in user to call the API
                viewModel.getLoggedInUser().observe(this, Observer { user ->
                    if (user != null) {
                        // Call the getInvoiceItemDetail API
                        viewModel.getInvoiceItemDetail(
                            strSearchBy,
                            user.UserId ?: 0,
                            customerId,
                            modelItem?.TicketID ?: 0,
                            scanType,
                            qrCode,
                            modelItem?.ProductID ?: 0
                        )

                        // Observe the invoiceItemResponse LiveData to get the response
                        viewModel.invoiceItemResponse.observe(this, Observer { response ->
                            // Re-enable the button
                            binding.tvGenerateInvoice.isClickable = true

                            // Log when the button is re-enabled after response
                            Log.d("GenerateInvoiceButton", "Response received - enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}")

                            if (response != null) {
                                // Successfully received the invoice item details
                                if (response.StatusCode == 200) {
                                    // Show dialog on success
                                    AlertDialog.Builder(this)
                                        .setTitle("Invoice Generated")
                                        .setMessage("Your invoice has been generated successfully.")
                                        .setPositiveButton("OK", null)
                                        .show()
                                } else {
                                    // Handle error response if needed
                                    AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Error: ${response.StatusCodeMessage}")
                                        .setPositiveButton("OK", null)
                                        .show()
                                }
                            } else {
                                // Handle null response
                                AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Error: No data received")
                                    .setPositiveButton("OK", null)
                                    .show()
                            }
                        })
                    } else {
                        // Handle the case when user is null (logged out)
                        AlertDialog.Builder(this)
                            .setTitle("User Not Logged In")
                            .setMessage("You are not logged in. Please log in to continue.")
                            .setPositiveButton("OK", null)
                            .show()

                        // Re-enable the button
                        binding.tvGenerateInvoice.isClickable = true

                        // Log when the button is re-enabled after user is null
                        Log.d("GenerateInvoiceButton", "User is null - enabling button: isClickable = ${binding.tvGenerateInvoice.isClickable}")
                    }
                })
            }
        }

    }
        /*override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            intent?.let {
                modelItem = it.getParcelableExtra(ARG_PARAM)
                scanType = it.getIntExtra("ScanType",1)
                qrCode = it.getStringExtra("QrCode").toString()
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

                    binding.txtCustName.text = modelItem?.CustName ?: "-"
                    binding.txtTktNumber.text = modelItem?.TicketNo ?: "-"
                    binding.txtAddress.text = modelItem?.CustAddress ?: "-"
                    binding.tvContactNo.text = modelItem?.CustContactNo ?: "-"
                    binding.tvEmailID.text = modelItem?.EmailID ?: "-"
                    binding.tvTktStatus.text = modelItem?.TicketStatus ?: "-"
                    initPaymentMethodSpinner()
                }
            })

            binding.tvAddItem.setOnClickListener {

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

            binding.tvGenerateInvoice.setOnClickListener {

                val jsArray = Gson().toJson(listAddedItems)
                val jsonArray = JSONArray(jsArray)
                Log.d("Arraduad ___", jsArray)

                if (listAddedItems?.isEmpty() == true) {
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
                                jsonArray,
                                strPaymentMethod,
                                binding.edtGstNo.text.toString()
                            )
                        }
                    })
                }

            }

        }*/

    private fun initPaymentMethodSpinner() {
        paymentMethod.add(PaymentMethodData("Select", 0))
        paymentMethod.add(PaymentMethodData("Cash", 1))
        paymentMethod.add(PaymentMethodData("Online", 2))

        val textFormatter = SpinnerTextFormatter<PaymentMethodData> { obj ->
            SpannableString(obj.PaymentMethod)
        }

        binding.spinnerPaymentMethod.setSpinnerTextFormatter(textFormatter)
        binding.spinnerPaymentMethod.setSelectedTextFormatter(textFormatter)
        binding.spinnerPaymentMethod.attachDataSource(paymentMethod)
        binding.spinnerPaymentMethod.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, view, position, id ->
                val item = binding.spinnerPaymentMethod.selectedItem as PaymentMethodData
                strPaymentMethod = item.PaymentMethod ?: ""
            }
    }

    private fun addItem() {
        itemPrice = binding.edtPrice.text.toString().toDouble()
        totalTaxPercent = binding.edtTax.text.toString().toDouble()
        totalQty = binding.edtQty.text.toString().toInt()
        if (totalQty > (selectedItem?.Qty ?: 0)) {
            toast("Quantity should be less than or equal to available quantity.")
            return
        }

        afterDiscountAmnt =
            ((itemPrice.toInt()) - (selectedItem?.DiscountAmt?.toInt() ?: 0))
        preTaxAmount = (itemPrice * totalQty).roundToInt()

        taxPercent1 = String.format("%.2f", totalTaxPercent / 2.0).toDouble()
        taxAmount1 = (preTaxAmount * (taxPercent1 / 100.0)).roundToInt()

        taxPercent2 = String.format("%.2f", totalTaxPercent / 2.0).toDouble()
        taxAmount2 = (preTaxAmount * (taxPercent2 / 100.0)).roundToInt()

        amountAfterTax = (preTaxAmount + taxAmount1 + taxAmount2).toDouble().roundToInt()

        addItemData = AddedInvoiceItemData(
            selectedItem?.ItemName ?: "-",
            0,
            selectedItem?.Slno ?: -1,
            totalQty,
            itemPrice,
            selectedItem?.DiscountPer ?: 0.0,
            selectedItem?.DiscountAmt?.toInt() ?: 0,
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
                            setItemValues(it[position])
                            selectedItem = it[position]
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
        binding.tvItemCode.text = data?.itemCode ?: "-"
        binding.tvItemColor.text = data?.ItemColor ?: "-"
        binding.tvItemcategory.text = data?.Category ?: "-"
        binding.tvItemSubCategory.text = data?.SubCategory ?: "-"
        binding.tvItemQuantity.text = data?.Qty.toString()

        binding.edtPrice.setText((data?.Rate ?: 0.0).toString())
        binding.edtTax.setText((data?.TaxPer ?: 0.0).toString())
        binding.edtQty.setText("1")
        binding.edtRoundoffAmount.setText((roundoffAmount).toString())
    }

    private fun List<AddedInvoiceItemData?>.toAddedInvoiceItem(): List<AddedInvoiceItem?> {
        return this.map {
            AddedInvoiceItem(it, this@GenerateInvoiceActivity, this@GenerateInvoiceActivity)
        }
    }

    private fun bindUI(list: List<AddedInvoiceItemData?>?) = Coroutines.main {
        list?.let {
            roundoffAmount = list.map { it?.FinalPrice ?: 0 }.sum()
            totalTaxAmount1 = list.map { it?.TaxAmount1 ?: 0 }.sum()
            totalTaxAmount2 = list.map { it?.TaxAmount2 ?: 0 }.sum()
            totalPreTaxAmount = list.map { it?.PreTaxAmount ?: 0 }.sum()
            totalDiscountAmount = list.map { it?.DiscountAmount ?: 0 }.sum()
            totalAfterDiscountAmount = list.map { it?.AfterDiscountAmount ?: 0 }.sum()

            binding.edtRoundoffAmount.setText(roundoffAmount.toString())
            initRecyclerView(it.toAddedInvoiceItem())

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

            if (data.isNotEmpty()) {
                initSpinnerItemList(data)
            }
        }

        if (callFrom.equals("invoice_generate")) {
            // binding.rootLayout.snackbar("Invoice generated successfully")
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Invoice generated successfully")

            builder.setPositiveButton(R.string.str_ok, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id12: Int) {
                    try {
                        finish()
                        refreshListener?.onRefresh()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
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