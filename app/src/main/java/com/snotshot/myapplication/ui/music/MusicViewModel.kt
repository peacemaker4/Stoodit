package com.snotshot.myapplication.ui.music

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Music page"
    }
    val text: LiveData<String> = _text
}