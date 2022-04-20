package com.goldmedal.crm.data.model

data class GetItemForInvoiceData(
        val Slno: Int,
        val ItemName: String,
        val itemCode: String,
        val ItemColor: String,
        val Category: String,
        val SubCategory: String,
        val Rate: Int,
        val TaxPer: Double,
        val TaxAmount1: Int,
        val TaxAmount2: Int,
        val TotalTaxAmount: Int,
        val FinalAmountAsMRP: Int,
        val DiscountPer: Double,
        val DiscountAmt: Int
)
