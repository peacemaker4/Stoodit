package com.snotshot.myapplication.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsersFragmentModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Our users"
    }
    val text: LiveData<String> = _text
}