package com.example.kleine.stripeApi



import com.example.kleine.BuildConfig
import com.example.kleine.model.AddPaymentLinkToStripe
import com.example.kleine.model.AddPriceToStripe
import com.example.kleine.model.AddProductToStripe
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface StripeApi {
    @POST("v1/products")
    @Headers("Authorization: Bearer " +BuildConfig.STRIPE_API_KEY)
    fun addStripeProduct(
        @Query("id") id: String?,
        @Query("name") name: String?,
        @Query("metadata[price]") price: Double,
        @Query("default_price_data[currency]") currency: String,
        @Query("default_price_data[unit_amount]") unitAmount: Int
    ): Call<AddProductToStripe>


    @POST("v1/prices")
    @Headers("Authorization: Bearer " +BuildConfig.STRIPE_API_KEY)
    fun addStripePrice(
        @Query("product") product: String?,
        @Query("currency") currency: String,
        @Query("unit_amount") unit_amount:Int
    ): Call<AddPriceToStripe>





    @POST("v1/payment_links")
    @Headers("Authorization: Bearer ${BuildConfig.STRIPE_API_KEY}")
    fun addStripePaymentLink(
        @Query("line_items[0]price]") id: String?,
        @Query("line_items[0][quantity]") quantity: Int
    ): Call<AddPaymentLinkToStripe>


}