package com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import java.io.Serializable

data class FoodDto(
    @SerializedName("yemek_id")
    val id: String,
    @SerializedName("yemek_adi")
    val name: String,
    @SerializedName("yemek_resim_adi")
    val imageName: String,
    @SerializedName("yemek_fiyat")
    val price: String): Serializable

fun FoodDto.toFood(): Food {
    return Food(
        id = id.toInt(),
        name = name,
        imageName = imageName,
        price = price.toDouble()
    )
}