package com.snotshot.myapplication.models

//data class Chat(val correspondent1: String ?= null, val correspondent2: String?= null)
data class Chat(val messages: MutableList<Message> ?= null, val correspondent1: String ?= null, val correspondent2: String?= null)
