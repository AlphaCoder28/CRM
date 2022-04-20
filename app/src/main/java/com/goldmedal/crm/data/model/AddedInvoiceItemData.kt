package com.goldmedal.crm.data.model

data class AddedInvoiceItemData( val itemName: String,
                                 val Slno: Int,
                                 val ItemID: Int,
                                 val Quantity: Int,
                                 val PricePerUnit: Double,
                                 val DiscountPercent: Double,
                                 val DiscountAmount: Int,
                                 val AfterDiscountAmount: Int,
                                 val TaxType: Int,
                                 val TaxAmount1: Int,
                                 val TaxAmount2: Int,
                                 val TaxPercent1: Double,
                                 val TaxPercent2: Double,
                                 val PreTaxAmount: Int,
                                 val FinalPrice: Int)
