package com.example.kleine.model

data class AddProductToStripe(

    val id:String,
    val name: String,
    val price:Double,
    val currency: String,
    val unit_amount: Int
)

{
    constructor() : this("","",0.0,"",0)
}

data class DefaultPriceData(
    val currency: String,
    val unit_amount: Int
) {
    constructor() : this("",0)
}


data class PaymentLinkResponse(
    val id: String,
    val objectType: String,
    val url: String
) {
    constructor() : this("", "", "")
}
