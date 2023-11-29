package com.w2c.kural.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.w2c.kural.repository.MainRepository

@Suppress("UNCHECKED_CAST")
class MainVMFactory(private val context: Context, private val repository: MainRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(repository) as T
    }
}
