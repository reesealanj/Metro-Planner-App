package com.example.daytripplanner_project1

import java.io.Serializable

data class Detail(
    val name: String,
    val pricePoint: String,
    val rating: Int,
    val address: String,
    val address2: String,
    val phone: String,
    val url: String,
    val type: Int
) : Serializable