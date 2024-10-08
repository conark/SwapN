package com.example.kleine.model

data class CartProduct (
    val id:String,
    val name:String,
    val store:String,
    val image:String,
    val price:Double,
    val newPrice:String?,
    val quantity:Int,
    val color:String,
    val size:String,
    val paymentLink: String?
        )
{
    constructor() : this("","","","",0.0,"",0,"","","")
}
