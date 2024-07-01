package com.example.kleine.viewmodel.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleine.model.AddPaymentLinkToStripe
import com.example.kleine.model.AddPriceToStripe
import com.example.kleine.model.AddProductToStripe
import com.example.kleine.stripeApi.StripeApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AddProductViewModel: ViewModel()  {
    fun createStripeProduct(id: String?, name: String?, price: Double){
        viewModelScope.launch {
            //成功したらAddproductを返して失敗したらnullになる
            val addProductResult = addStripeProduct(id,name,price).getOrNull() ?: return@launch
            val addPriceResult = addStripePrice(id,price).getOrNull() ?: return@launch
            val quantity = 1
            val addPaymentLinkResult = addStripePaymentLink(id,quantity).getOrNull() ?: return@launch
//            val paymentLinkUrl = addUrlResult.url
//            Log.d("PaymentLinkURL", paymentLinkUrl)

        }
    }

    private suspend fun addStripeProduct(id: String?, name: String?, price: Double): Result<AddProductToStripe> {
        return withContext(Dispatchers.IO) {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.stripe.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            try {
                val service: StripeApi = retrofit.create(StripeApi::class.java)
                val response = service.addStripeProduct(
                    id = id,
                    name = name,
                    price = price,
                    currency = "eur",
                    unitAmount = (price * 100).toInt(),
                ).execute()

                if (response.isSuccessful) {
                    val stripeApiResponse = response.body()
                    if (stripeApiResponse != null) {
                        Log.d("response-stripe", stripeApiResponse.toString())
                        return@withContext Result.success(stripeApiResponse)
                    } else {
                        throw IllegalStateException("bodyがnullだよ！")
                    }
                } else {
                    Log.d("response-stripe", "Request failed with response code: ${response.code()}")
                    Log.d("response-stripe", "Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d("response-stripe", "debug $e")
            }
            Result.failure(Exception("Stripe Product Request fail"))
        }
    }
    private suspend fun addStripePrice(product: String?, price: Double): Result<AddPriceToStripe>  {
        return withContext(Dispatchers.IO) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()


        try {
            val service: StripeApi = retrofit.create(StripeApi::class.java)
            val response = service.addStripePrice(
                product = product,
                currency = "eur",
                unit_amount = (price * 100).toInt()
            ).execute()

            if (response.isSuccessful) {
                val stripeApiResponse = response.body()
                if (stripeApiResponse != null) {

                    Log.d("response-stripe", stripeApiResponse.toString())
                    return@withContext Result.success(stripeApiResponse)

                } else {
                    throw IllegalStateException("bodyがnullだよ！")
                }
            } else {
                Log.d("response-stripe", "Request failed with response code: ${response.code()}")
                Log.d("response-stripe", "Error body: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d("response-stripe", "debug $e")
        }
        Result.failure(Exception("Stripe Price Request fail"))
        }
    }

    private suspend fun addStripePaymentLink(price: String?,quantity:Int): Result<AddPaymentLinkToStripe>  {
        return withContext(Dispatchers.IO) {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.stripe.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()


            try {
                val service: StripeApi = retrofit.create(StripeApi::class.java)
                val response = service.addStripePaymentLink(
                    id = price,
                    quantity = quantity

                ).execute()

                if (response.isSuccessful) {
                    val stripeApiResponse = response.body()
                    if (stripeApiResponse != null) {

                        Log.d("response-stripe", stripeApiResponse.toString())
                        return@withContext Result.success(stripeApiResponse)

                    } else {
                        throw IllegalStateException("bodyがnullだよ！")
                    }
                } else {
                    Log.d("response-stripe", "Request failed with response code: ${response.code()}")
                    Log.d("response-stripe", "Error body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.d("response-stripe", "debug $e")
            }
            Result.failure(Exception("リクエスト失敗"))
        }

    }


}