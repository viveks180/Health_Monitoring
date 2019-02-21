package com.healthmonitoring.model

import java.util.*

data class User(
    var email: String = "",
    var password: String = "",
    var name: String = "",
    var weight: Int = 0,
    var age: Int = 0,
    var gender: Int = 0,
    var height: Int = 0,
    var bpm: Int = 0,
    var value: Int = 0,
    var ibi: Int = 0,
    var temperature: Float = 0f,
    var lastUpdated: Date? = null)