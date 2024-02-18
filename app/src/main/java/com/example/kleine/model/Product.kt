package com.example.kleine.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class Product(
    val id:String?,
    val title: String? = "",
    val description: String? = "",
    val category: String? = "",
    val newPrice:String?="",
    val price: String? = "",
    val seller: String? = "",
    val images: List<String>,
    val colors: List<Long>?,
    val sizes:List<String>?,
//    val images:@RawValue HashMap<String, Any>?=null,
//    val colors:@RawValue HashMap<String, Any>?=null,
//    val sizes:@RawValue HashMap<String, Any>?=null,
    val orders:Int = 0,
    val offerTime:Date? = null,
    val sizeUnit:String?=null

) : Parcelable
    {
    constructor(
         id :String,
         title: String? = "",
         description: String? = "",
         category: String? = "",
         price: String? = "",
         seller: String? = "",
         images: List<String>,
         colors: List<Long>,
         sizes: List<String>
    ) : this(id,title,description,category,null,price,seller, images, colors, sizes,0,null,null)


        constructor():this("", "", "", "", "", "", listOf(), listOf(), listOf())
    //constructor():this("","","","","",null,null,null)
}
