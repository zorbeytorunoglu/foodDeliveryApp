package com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetFoodsResponse(
    @SerializedName("yemekler")
    val foods: List<FoodDto>,
    val success: Int
)