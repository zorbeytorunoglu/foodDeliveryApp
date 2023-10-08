package com.zorbeytorunoglu.fooddeliveryapp.data.remote

import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.CRUDResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsInCartResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {

    @GET("yemekler/tumYemekleriGetir.php")
    suspend fun getFoods(): GetFoodsResponse


    @POST("yemekler/sepeteYemekEkle.php")
    @FormUrlEncoded
    suspend fun addFoodToCart(
        @Field("yemek_adi") name: String,
        @Field("yemek_resim_adi") imageName: String,
        @Field("yemek_fiyat") price: Int,
        @Field("yemek_siparis_adet") amount: Int,
        @Field("kullanici_adi") userUuid: String
    ): CRUDResponse


    @POST("yemekler/sepettekiYemekleriGetir.php")
    @FormUrlEncoded
    suspend fun getFoodsInCart(@Field("kullanici_adi") userUuid: String): GetFoodsInCartResponse


    @POST("yemekler/sepettenYemekSil.php")
    @FormUrlEncoded
    suspend fun removeFoodFromCart(
        @Field("sepet_yemek_id") foodInCartId: Int,
        @Field("kullanici_adi") userUuid: String
    ): CRUDResponse

}