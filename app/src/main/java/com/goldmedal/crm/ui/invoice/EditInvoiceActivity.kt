package com.goldmedal.crm.ui.invoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.*
import com.goldmedal.crm.databinding.ActivityEditInvoiceBinding
import com.goldmedal.crm.ui.auth.WebActivity
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toast
import kotlinx.android.synthetic.main.activity_edit_invoice.*
import kotlinx.android.synthetic.main.activity_edit_invoice.txtCustName
import kotlinx.android.synthetic.main.activity_edit_invoice.txtTktNumber
import kotlinx.android.synthetic.main.activity_generate_invoice.*
import org.angmarch.views.OnSpinnerItemSelectedListener
import org.angmarch.views.SpinnerTextFormatter
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class EditInvoiceActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var binding: ActivityEditInvoiceBinding
    private var invoiceData: EditInvoiceData? = null

    var strPaymentMethod: String = ""
    var strPaymentStatus: String = ""
    var boolPaymentStatus: Boolean = false
    var invoiceNo: Int = 0

    val paymentMethod: MutableList<PaymentMethodData> = ArrayList()
    val paymentStatus: MutableList<PaymentStatusData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        invoiceNo = intent.getIntExtra(INVOICE_NO,0)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        paymentMethod.add(PaymentMethodData("Select", 0))
        paymentMethod.add(PaymentMethodData("Cash", 1))
        paymentMethod.add(PaymentMethodData("Online", 2))

        val textFormatter1 =
            SpinnerTextFormatter<PaymentMethodData> { obj ->
                SpannableString(
                    obj.PaymentMethod
                )
            }
        binding.spinnerPaymentMethod.setSpinnerTextFormatter(textFormatter1)
        binding.spinnerPaymentMethod.setSelectedTextFormatter(textFormatter1)
        binding.spinnerPaymentMethod.attachDataSource(paymentMethod)
        binding.spinnerPaymentMethod.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, view, position, id ->
                val item = binding.spinnerPaymentMethod.selectedItem as PaymentMethodData
                strPaymentMethod = item.PaymentMethod ?: ""
            }

        paymentStatus.add(PaymentStatusData("Select", false))
        paymentStatus.add(PaymentStatusData("Unpaid", false))
        paymentStatus.add(PaymentStatusData("Paid", true))

        val textFormatter2 =
            SpinnerTextFormatter<PaymentStatusData> { obj ->
                SpannableString(
                    obj.PaymentStatus
                )
            }
        binding.spinnerPaymentStatus.setSpinnerTextFormatter(textFormatter2)
        binding.spinnerPaymentStatus.setSelectedTextFormatter(textFormatter2)
        binding.spinnerPaymentStatus.attachDataSource(paymentStatus)
        binding.spinnerPaymentStatus.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, view, position, id ->
                val item = binding.spinnerPaymentStatus.selectedItem as PaymentStatusData
               boolPaymentStatus = item.ActionId ?: false
                strPaymentStatus = item.PaymentStatus ?: ""
            }


        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.editInvoiceDetail(
                    invoiceNo
                )
            }
        })


       // - - - - - - click on update status button
        tvUpdateStatus.setOnClickListener {
            viewModel.getLoggedInUser().observe(this, Observer { user ->
                if (user != null && invoiceData != null) {
                    if(strPaymentStatus.equals("Select")){
                        toast("Select Payment Status")
                    }else if(strPaymentMethod.equals("Select")){
                        toast("Select Payment Method")
                    } else {
                        viewModel.updateInvoiceDetail(
                            invoiceData?.SlNo ?: 0,
                            strPaymentMethod,
                            boolPaymentStatus,
                            invoiceData?.PaymentStatus ?: "",
                            user.UserId ?: 0
                        )
                    }
                }else{
                    toast("Invalid Invoice")
                }
            })
        }
    }


    override fun onStarted(callFrom: String) {

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        if(callFrom.equals("edit_invoice_detail")){
            val data = _object as List<EditInvoiceData?>

            if (data.count() > 0) {
                invoiceData = data[0]
                bindUI(data[0])
            }
        }

        if(callFrom.equals("update_invoice")){
            val data = _object as List<UpdateInvoiceData?>
            if (data.count() > 0) {
                toast("Invoice Updated Successfully")
                InvoiceListActivity.start(this)
                finish()
            }else{
                toast("Invoice cannot be updated at this moment. Please try again later!")
            }

        }

    }

    private fun bindUI(modelItem: EditInvoiceData?) = Coroutines.main {
        modelItem?.let {
            txtCustName.text = modelItem.CustName
            txtTktNumber.text = modelItem.TktNo

            if(modelItem.IsPaid == 0){
                txtAmountPaid.text ="No"
            }else{
                txtAmountPaid.text = "Yes"
            }
            txtFinalAmount.text = modelItem.FinalTotal

            strPaymentMethod = modelItem.PaymentMethod
            strPaymentStatus = modelItem.PaymentStatus

            if(modelItem.PaymentStatus.lowercase(Locale.getDefault()) == "paid"){
                boolPaymentStatus = true
                strPaymentStatus = "Paid"
                spinner_payment_status.text = strPaymentStatus
            }else if(modelItem.PaymentStatus.lowercase(Locale.getDefault()) == "unpaid"){
                boolPaymentStatus = false
                strPaymentStatus = "UnPaid"
                spinner_payment_status.text  = strPaymentStatus
            }else{
                boolPaymentStatus = false
                strPaymentStatus = "Select"
                spinner_payment_status.text  = strPaymentStatus
            }

            if(modelItem.PaymentMethod.lowercase(Locale.getDefault()) == "cash"){
                strPaymentMethod = "Cash"
                binding.spinnerPaymentMethod.text = strPaymentMethod
            }else if(modelItem.PaymentMethod.lowercase(Locale.getDefault()) == "online"){
                strPaymentMethod = "Online"
                binding.spinnerPaymentMethod.text  = strPaymentMethod
            }else{
                strPaymentMethod = "Select"
                binding.spinnerPaymentMethod.text  = strPaymentMethod
            }


        }
    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
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
        const val INVOICE_NO = "invoice_no"

        fun start(
            context: Context,
            invoiceNo: Int
        ) {
            val intent = Intent(context, EditInvoiceActivity::class.java)
            intent.putExtra(INVOICE_NO, invoiceNo)
            context.startActivity(intent)
        }
    }
}

