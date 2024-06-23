package com.example.kleine.model

data class Stripe(

    val id:String,
    val name: String,
    val price:Double,
)

{
    constructor() : this("","",0.0)
}


data class PaymentLinkResponse(
    val id: String,
    val objectType: String,
    val url: String
) {
    constructor() : this("", "", "")
}
