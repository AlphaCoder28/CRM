package com.goldmedal.crm.data.model

data class GetItemForInvoiceData(
        val Slno: Int,
        val ItemName: String,
        val itemCode: String,
        val ItemColor: String,
        val Category: String,
        val SubCategory: String,
        val Rate: Double,
        val TaxPer: Double,
        val TaxAmount1: Double,
        val TaxAmount2: Double,
        val TotalTaxAmount: Double,
        val FinalAmountAsMRP: Double,
        val DiscountPer: Double,
        val DiscountAmt: Double
)
