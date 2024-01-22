package com.example.kleine.viewmodel.lunchapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kleine.firebaseDatabase.FirebaseDb

//class ViewModelProviderFactory(
//    private val firebaseDb: FirebaseDb
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return KleineViewModel(firebaseDb) as T
//    }
//}

class ViewModelProviderFactory(
    private val firebaseDb: FirebaseDb
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T  {
        // ViewModelProvider.Factoryのcreateメソッドでは、返り値が指定された型のViewModelである必要があります。
        if (modelClass.isAssignableFrom(KleineViewModel::class.java)) {
            // KleineViewModelが指定された型の場合、FirebaseDbを引数にしてインスタンスを作成して返します。
            return KleineViewModel(firebaseDb) as T
        }
        // それ以外の型が指定された場合はエラーを投げるか、nullを返すか、デフォルトの処理を行います。
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}