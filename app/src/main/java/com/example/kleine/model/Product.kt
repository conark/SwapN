package com.example.kleine.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Product(
    val id:String?,
    val title: String? = "",
    val description: String? = "",
    val category: String? = "",
 //   val newPrice:Double = 0.0,
    val price: Double = 0.0,
    val images: List<String>,
    val colors: List<Int>?,
    val sizes:List<String>?,
    val storeUid: String? = "",
    val orders:Int = 0,
    val offerTime:Date? = null,
    val sizeUnit:String?=null,
    val paymentLinkUrl: String? = null

) : Parcelable
    {
    constructor(
        id:String,
        title: String? = "",
        description: String? = "",
        category: String? = "",
        price: Double = 0.0,
        images: List<String>,
        colors: List<Int>?,
        sizes: List<String>?,
        storeUid: String? = "",
    ) : this(id,title,description,category,price,images, colors,
        sizes,storeUid,0,null,null,null)



        constructor():this("", "", "", "", 0.0, listOf(), listOf(), listOf(),"")
}
