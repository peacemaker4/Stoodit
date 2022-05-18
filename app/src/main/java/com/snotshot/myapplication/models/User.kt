package com.snotshot.myapplication.models

data class User(val uid: String ?= null, val username: String ?= null, val email: String ?= null, val university: String ?= null, val year: String ?= null, val group: String ?= null, val description: String ?= null, val picture: String ?= null) {

}