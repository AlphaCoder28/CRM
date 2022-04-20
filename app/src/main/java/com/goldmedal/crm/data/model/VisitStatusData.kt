package com.goldmedal.crm.data.model


data class VisitStatusData(
    val VisitStatus: String? = null
    , val ActionId: Int? = null
)

data class PaymentStatusData(
    val PaymentStatus: String? = null
    , val ActionId: Boolean? = false
)

data class PaymentMethodData(
    val PaymentMethod: String? = null
    , val ActionId: Int? = null
)