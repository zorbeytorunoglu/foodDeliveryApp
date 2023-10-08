package com.zorbeytorunoglu.fooddeliveryapp.domain.repository

import androidx.lifecycle.LiveData
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.CRUDResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsInCartResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsResponse
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.GeneralState
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface FoodRepository {

    val cartLiveData: LiveData<List<FoodInCart>>

    val addFoodToCartState: StateFlow<GeneralState>

    val removeFoodFromCartState: StateFlow<GeneralState>

    val getFoodsInCartState: StateFlow<State<List<FoodInCart>>>

    fun updateCartLiveData(foodInCartList: List<FoodInCart>)

    fun updateAddFoodToCartState(state: GeneralState)

    fun updateRemoveFoodFromCartState(state: GeneralState)

    fun updateGetFoodsInCartState(state: State<List<FoodInCart>>)

    suspend fun getFoods(): GetFoodsResponse

    suspend fun addFoodToCart(food: Food, amount: Int, userUuid: String): CRUDResponse

    suspend fun removeFoodFromCart(foodInCartId: Int, userUuid: String): CRUDResponse

    suspend fun getFoodsInCart(userUuid: String): GetFoodsInCartResponse

}