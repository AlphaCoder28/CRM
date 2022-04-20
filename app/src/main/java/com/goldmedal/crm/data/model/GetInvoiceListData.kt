package com.goldmedal.crm.data.model

data class GetInvoiceListData(
        val CustAddress: String,
        val CustContactNo: String,
        val CustName: String,
        val EmailID: String,
        val FinalTotal: String,
        val InvoiceNumber: String,
        val PaymentMethod: String,
        val PaymentStatus: String,
        val PreTaxAmount: String,
        val Slno: Int,
        val TaxAmount: String,
        val InvoicePDF: String,
        val TktNo: String
)
