package com.snotshot.myapplication.ui.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatsFragmentModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your Chats"
    }
    val text: LiveData<String> = _text
}