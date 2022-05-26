package com.snotshot.myapplication.models

data class Message(val text: String ?= null, val time: String ?= null, var sender: String ?= null) {

}