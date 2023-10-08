package com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto

data class CRUDResponse(
    val success: Int,
    val foodList: List<FoodDto>
)