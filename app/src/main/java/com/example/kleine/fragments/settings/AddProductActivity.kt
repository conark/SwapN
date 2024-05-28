package com.example.kleine.fragments.settings


import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.kleine.R
import com.example.kleine.databinding.ActivityAddproductBinding
import com.example.kleine.model.Product
import com.example.kleine.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID



class AddProductActivity :  AppCompatActivity() {

    private val binding by lazy { ActivityAddproductBinding.inflate(layoutInflater) }
    private var selectedImages = mutableListOf<Uri>()
    private var selectedColors = mutableListOf<Int>()
    private var productsStorage = Firebase.storage.reference
    private val firestore = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)


        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog.Builder (this)
                .setTitle("Product colour")
                .setPositiveButton("Select",object:ColorEnvelopeListener {
                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            selectedColors.add(it.color)
                            updateColors()
                        }
                    }
                })
                .setNegativeButton("Cancel"){colorPicker,_ ->
                    colorPicker.dismiss()
                }.show()

        }

        val selectImagesActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
                if(result.resultCode == RESULT_OK){
                    val intent = result.data

                    //multiple image select
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?:0
                        (0  until  count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedImages.add(it)
                            }
                        }
                    }else {
                        val imageUri = intent?.data
                        imageUri?.let {selectedImages.add(it)}
                    }
                    updateImages()
                }

            }
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent (ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }


    }



    private fun updateImages() {
        binding.tvSelectedImages.text = selectedImages.size.toString()
    }

    private fun updateColors() {
        var colors = ""
        selectedColors.forEach {
            colors = "$colors ${Integer.toHexString(it)}"
        }
        binding.tvSelectedColors.text = colors
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.back -> {
                onBackPressed() // 前の画面
                return true
            }

            R.id.saveProduct -> {
                val productValidation = validateInformation()
                if (!productValidation) {
                    Toast.makeText(this, "Check your inputs", Toast.LENGTH_SHORT).show()
                    return false
                }
                saveProduct()
                return true

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveProduct() {
        val title = binding.edName.text.toString().trim()
        val category = binding.edCategory.text.toString().trim()
        val price = binding.edPrice.text.toString().trim()
        val offerPercentage = binding.offerPercentage.text.toString().trim()
        val description = binding.edDescription.text.toString().trim()
        val sizes = getSizeList (binding.edSizes.text.toString().trim())
        val imagesByteArrays = getImagesByteArrays()
        val images = mutableListOf<String>()




        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                showLoading()
            }

            try {
                async {
                    imagesByteArrays.forEach {
                        val id = UUID.randomUUID().toString()
                        launch {
                            val imageStorage = productsStorage.child("products/images/$id")
                            val result = imageStorage.putBytes(it).await()
                            val downloadUrl = result.storage.downloadUrl.await().toString()
                            images.add(downloadUrl)

                        }
                    }
                }.await()
            }catch (e: Exception){
                e.printStackTrace()
                withContext(Dispatchers.Main){
                    hideLoading()
                }
            }
 //           val userName = fetchUserName()
            val userId = fetchUserId()
            val product = Product (
                UUID.randomUUID().toString(),
                title,
                if (description.isEmpty()) null else description,
                category,
                price.toDouble(),
                images,
                if (selectedColors.isEmpty()) null else selectedColors,
                sizes,
   //             userName
                userId

            )


            firestore.collection("products").add(product).addOnSuccessListener {
                hideLoading()
//                    // Check if storeUid already exists
//                    firestore.collection("stores")
//                        .whereEqualTo("storeUid",userId)
//                        .get()
//                        .addOnSuccessListener { querySnapshot ->
//                            if (querySnapshot.isEmpty) {// storeid does not exist, so add it
//                                val store = Store(
//                                    name = storeName,
//                                    uid = userId
//                                )
//                                firestore.collection("stores").add(store).addOnSuccessListener {
//                                    Log.d("Success", "Store added successfully")
//                                }.addOnFailureListener { e ->
//                                    Log.e("Error", "Failed to add store: ${e.message}")
//                                }
//                            } else {
//                                Log.d("Info", "Store id already exists")
//                            }
//                        }
            }.addOnFailureListener{
                hideLoading()
                Log.e("Error",it.message.toString())
            }


        }
    }

    private fun hideLoading() {
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showLoading() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun getImagesByteArrays(): List<ByteArray> {
        val imagesByteArray = mutableListOf<ByteArray>()
        selectedImages.forEach{
            val stream = ByteArrayOutputStream()
            val imageBmp = MediaStore.Images.Media.getBitmap(contentResolver,it)
            if (imageBmp.compress(Bitmap.CompressFormat.JPEG,100,stream)){
                imagesByteArray.add(stream.toByteArray())
            }
        }
        return imagesByteArray

    }

    private fun getSizeList(sizesStr: String): List<String>? {
        if (sizesStr.isEmpty())
            return null
        val sizesList = sizesStr.split(",")
        return sizesList

    }

    private fun validateInformation(): Boolean {
        if (binding.edPrice.text.toString().trim().isEmpty())
            return false

        if (binding.edName.text.toString().trim().isEmpty())
            return false

        if (binding.edCategory.text.toString().trim().isEmpty())
            return false

        if (selectedImages.isEmpty())
            return false
        return true
    }

    private suspend fun fetchUser(): User? {
        return withContext(Dispatchers.IO) {
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserUid != null) {
                val userDocument = FirebaseFirestore.getInstance().collection("users").document(currentUserUid).get().await()
                val user = userDocument.toObject(User::class.java)
                user
            } else {
                null
            }
        }
    }

    private suspend fun fetchUserName(): String? {
        val user = fetchUser()
        return user?.userName
    }

    private fun fetchUserId(): String? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid
    }



}