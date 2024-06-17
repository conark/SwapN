package com.example.kleine.model

data class Stripe(

    val id:String,
    val name: String,
    val price:Double,
)

{
    constructor() : this("","",0.0)
}
