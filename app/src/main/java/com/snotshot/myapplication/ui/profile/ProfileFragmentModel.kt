package com.snotshot.myapplication.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileFragmentModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your profile!"
    }
    val text: LiveData<String> = _text
}