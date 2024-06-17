package com.example.kleine.stripeApi



import com.example.kleine.model.Stripe
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface StripeApi {
    @POST("v1/products")
    @Headers(BuildConfig.OWM_API_KEY)
    fun addStripeProduct(
        @Query("q") id: String?,
        @Query("name") name: String?,
        @Query("price") price: Double,
    ): Call<Stripe>
}