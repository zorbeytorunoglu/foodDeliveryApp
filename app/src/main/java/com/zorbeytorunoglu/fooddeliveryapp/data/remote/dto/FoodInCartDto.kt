package com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FoodInCartDto(
    @SerializedName("sepet_yemek_id")
    val id: String,
    @SerializedName("yemek_adi")
    val name: String,
    @SerializedName("yemek_resim_adi")
    val imageName: String,
    @SerializedName("yemek_fiyat")
    val price: String,
    @SerializedName("yemek_siparis_adet")
    val amount: String,
    @SerializedName("kullanici_adi")
    val userUuid: String
) {
}