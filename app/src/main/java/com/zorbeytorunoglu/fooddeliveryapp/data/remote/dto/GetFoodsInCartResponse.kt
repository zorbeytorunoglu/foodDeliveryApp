package com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GetFoodsInCartResponse(
    @SerializedName("sepet_yemekler")
    val foods: List<FoodInCartDto>,
    val success: Int
) {
}