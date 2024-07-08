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
    val id:String,
    val product:String,
    val currency: String,
    val unit_amount: Int,
)



data class AddPaymentLinkToStripe(
    val lineItems:List <LineItem>,
    val url : String
)
data class LineItem(
    val price: String?,
    val quantity: Int,
    val adjustable_quantity: Map<String, Adjustable_Quantity>,

)


data class Adjustable_Quantity(

    val enabled: Boolean
)