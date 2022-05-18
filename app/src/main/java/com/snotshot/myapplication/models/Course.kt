package com.snotshot.myapplication.models

data class Course(val subject: String ?= null, val credit: Int ?= null, val total: Int ?= null, var uid: String ?= null) {

}