package com.snotshot.myapplication.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotesFragmentModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your notes"
    }
    val text: LiveData<String> = _text
}