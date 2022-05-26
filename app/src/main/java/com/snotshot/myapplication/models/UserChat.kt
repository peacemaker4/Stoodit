package com.snotshot.myapplication.models

//data class UserChat(val chat_uid: String ?= null, var contact_uid: String ?= null)
data class UserChat(val last_message: Message ?= null, val chat_uid: String ?= null, var contact_uid: String ?= null)