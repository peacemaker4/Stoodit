package com.snotshot.myapplication.ui.premium

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PremiumViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Premium page"
    }
    val text: LiveData<String> = _text
}