package com.goldmedal.crm.ui.invoice

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetInvoiceListData
import com.goldmedal.crm.databinding.InvoiceListRowBinding
import com.goldmedal.crm.ui.auth.WebActivity
import com.xwray.groupie.viewbinding.BindableItem

class InvoiceListItem(
    private val invoiceListData: GetInvoiceListData?,
    private val context: Context
) : BindableItem<InvoiceListRowBinding>() {

    override fun bind(viewBinding: InvoiceListRowBinding, position: Int) {

        viewBinding.apply {

            txtInvAddress.text = invoiceListData?.CustAddress.toString()
            txtInvCustName.text = invoiceListData?.CustName.toString()
            txtInvCustNo.text = invoiceListData?.CustContactNo.toString()
            txtInvFinalAmount.text = invoiceListData?.FinalTotal.toString()
            txtInvNumber.text = invoiceListData?.InvoiceNumber.toString()
            txtInvPaymentMethod.text = invoiceListData?.PaymentMethod.toString()
            txtInvPaymentStatus.text = invoiceListData?.PaymentStatus.toString()
            txtInvPreTaxAmount.text = invoiceListData?.PreTaxAmount.toString()
            txtInvTaxAmount.text = invoiceListData?.TaxAmount.toString()
            txtInvTktNo.text = invoiceListData?.TktNo.toString()

            btnViewPdf.setOnClickListener {
                if(!invoiceListData?.InvoicePDF.isNullOrEmpty()){
                    WebActivity.start(context,invoiceListData?.InvoicePDF ?: "")
                }
            }

            btnEditInvoice.setOnClickListener {
                    EditInvoiceActivity.start(context,(invoiceListData?.Slno ?: 0))
            }

        }
    }

    override fun getLayout() = R.layout.invoice_list_row


    override fun initializeViewBinding(view: View) = InvoiceListRowBinding.bind(view)


}