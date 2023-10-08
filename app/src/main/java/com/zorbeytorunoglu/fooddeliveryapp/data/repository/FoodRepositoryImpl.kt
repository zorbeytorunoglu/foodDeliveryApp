package com.zorbeytorunoglu.fooddeliveryapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.FoodApi
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.CRUDResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsInCartResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsResponse
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.FoodListState
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.GeneralState
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val api: FoodApi
): FoodRepository {

    private val _foodListState = MutableStateFlow(FoodListState())
    override val foodListState: StateFlow<FoodListState>
        get() = _foodListState

    private val _cartLiveData = MutableLiveData<List<FoodInCart>>()

    override val cartLiveData: LiveData<List<FoodInCart>>
        get() = _cartLiveData

    private val _addFoodToCartState = MutableStateFlow(GeneralState())
    override val addFoodToCartState: StateFlow<GeneralState>
        get() = _addFoodToCartState

    private val _removeFoodFromCartState = MutableStateFlow(GeneralState())
    override val removeFoodFromCartState: StateFlow<GeneralState>
        get() = _removeFoodFromCartState

    private val _getFoodsInCartState = MutableStateFlow(State<List<FoodInCart>>())
    override val getFoodsInCartState: StateFlow<State<List<FoodInCart>>>
        get() = _getFoodsInCartState

    override fun updateFoodListState(foodListState: FoodListState) {
        _foodListState.value = foodListState
    }

    override fun updateCartLiveData(foodInCartList: List<FoodInCart>) {
        _cartLiveData.value = foodInCartList
    }

    override fun updateAddFoodToCartState(state: GeneralState) {
        _addFoodToCartState.value = state
    }

    override fun updateRemoveFoodFromCartState(state: GeneralState) {
        _removeFoodFromCartState.value = state
    }

    override fun updateGetFoodsInCartState(state: State<List<FoodInCart>>) {
        _getFoodsInCartState.value = state
    }

    override suspend fun getFoods(): GetFoodsResponse = api.getFoods()

    override suspend fun addFoodToCart(food: Food, amount: Int, userUuid: String): CRUDResponse {
        return api.addFoodToCart(food.name,food.imageName,food.price.toInt(),amount, userUuid)
    }

    override suspend fun removeFoodFromCart(foodInCartId: Int, userUuid: String): CRUDResponse {
        return api.removeFoodFromCart(foodInCartId, userUuid)
    }

    override suspend fun getFoodsInCart(userUuid: String): GetFoodsInCartResponse {
        return api.getFoodsInCart(userUuid)
    }

}