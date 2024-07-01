package com.example.kleine.model

data class AddProductToStripe(

    val id:String,
    val name: String,
    val metadata: Metadata,

)



data class Metadata (
    val price : String
)

data class AddPriceToStripe(
    val product:String,
    val currency: String,
    val unit_amount: Int,
)


data class AddPaymentLinkToStripe(
    val lineItems:List<LineItem>
)
data class LineItem(
    val price: String?,
    val quantity: Int
)