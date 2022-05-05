package com.snotshot.myapplication.ui.courses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CoursesFragmentModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Your Courses"
    }
    val text: LiveData<String> = _text
}