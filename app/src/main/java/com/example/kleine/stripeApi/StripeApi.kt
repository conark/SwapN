package com.example.kleine.stripeApi



import com.example.kleine.BuildConfig
import com.example.kleine.model.PaymentLinkResponse
import com.example.kleine.model.Stripe
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface StripeApi {
    @POST("v1/products")
    @Headers(BuildConfig.STRIPE_API_KEY)
    fun addStripeProduct(
        @Query("q") id: String?,
        @Query("name") name: String?,
        @Query("price") price: Double,
    ): Call<Stripe>

    @POST("v1/payment_links")
    @Headers("Authorization: Bearer ${BuildConfig.STRIPE_API_KEY}")
    fun createPaymentLink(
        @Query("line_items[0][price]") priceId: String,
        @Query("line_items[0][quantity]") quantity: Int = 1
    ): Call<PaymentLinkResponse>
}