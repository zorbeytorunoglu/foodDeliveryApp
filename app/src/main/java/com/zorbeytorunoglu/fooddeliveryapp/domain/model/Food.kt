package com.zorbeytorunoglu.fooddeliveryapp.domain.model

import java.io.Serializable

data class Food(
    val id: Int,
    val name: String,
    val imageName: String,
    val price: Double
): Serializable