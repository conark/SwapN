package com.example.kleine.viewmodel.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kleine.model.AddPaymentLinkToStripe
import com.example.kleine.model.AddPriceToStripe
import com.example.kleine.model.AddProductToStripe
import com.example.kleine.stripeApi.StripeApi
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AddProductViewModel: ViewModel()  {

    private val firestore = FirebaseFirestore.getInstance()
    fun createStripeProduct(id: String?,name: String?, price: Double){
        viewModelScope.launch {
            //成功したらAddproductを返して失敗したらnullになる
            val addProductResult = addStripeProduct(id,name,price).getOrNull() ?: return@launch
            // プロダクトID取得
            val product = addProductResult.id

            val addPriceResult = addStripePrice(product,price).getOrNull() ?: return@launch
            // 価格ID取得
            val priceId = addPriceResult.id
            val quantity = 1
            val addPaymentLinkResult = addStripePaymentLink(priceId,quantity,adjustableQuantityEnabled  = true).getOrNull() ?: return@launch
            val paymentLink = addPaymentLinkResult.url
            Log.d("PaymentLink", paymentLink)
            savePaymentLinkToFirebase(id, paymentLink)

        }
    }

    private fun savePaymentLinkToFirebase(id: String?, paymentLink: String) {
        if (id == null) return

        val productsCollection = firestore.collection("products")
        productsCollection.whereEqualTo("id", id).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docId = document.id
                    productsCollection.document(docId).update("paymentLink", paymentLink)
                        .addOnSuccessListener {
                            Log.d("Firebase", "Payment link successfully added!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firebase", "Error adding payment link", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firebase", "Error getting documents: ", e)
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
    private suspend fun addStripePrice(productId: String?, price: Double): Result<AddPriceToStripe>  {
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

                product = productId,
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

    private suspend fun addStripePaymentLink(priceId: String?,quantity:Int,adjustableQuantityEnabled:Boolean): Result<AddPaymentLinkToStripe>  {
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
                    price = priceId,
                    quantity = quantity ,
                    adjustableQuantityEnabled =  adjustableQuantityEnabled
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