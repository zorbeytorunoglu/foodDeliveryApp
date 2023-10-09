package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.GroupedCartFood
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.add_food_to_cart.AddFoodToCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart.GetFoodsInCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_user.GetUserUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.remove_food_from_cart.RemoveFoodFromCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartFragmentViewModel @Inject constructor(
    repository: FoodRepository,
    private val getFoodsInCartUseCase: GetFoodsInCartUseCase,
    private val removeFoodFromCartUseCase: RemoveFoodFromCartUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val addFoodToCartUseCase: AddFoodToCartUseCase
) : ViewModel() {

    val cartLiveData = repository.cartLiveData

    init {
        getFoodsInCart()
    }

    fun addFoodToCart(foodInCart: FoodInCart, updateCart: Boolean) {
        addFoodToCartUseCase(foodInCart.food, 1, getUserUseCase().uid, updateCart, viewModelScope)
    }

    fun getFoodsInCart() {
        getFoodsInCartUseCase(viewModelScope)
    }

    fun removeFoodFromCart(foodInCart: FoodInCart, updateCart: Boolean) {
        removeFoodFromCartUseCase(foodInCart.food, getUserUseCase().uid, updateCart, viewModelScope)
    }

    fun groupAndCalculateTotal(foodInCartList: List<FoodInCart>?): List<GroupedCartFood> {

        if (foodInCartList == null) return emptyList()

        val groupedFoodMap = mutableMapOf<String, GroupedCartFood>()

        for (foodInCart in foodInCartList) {
            val foodName = foodInCart.food.name
            val totalPrice = foodInCart.food.price

            if (groupedFoodMap.containsKey(foodName)) {
                val existingGroup = groupedFoodMap[foodName]!!
                existingGroup.quantity++
                existingGroup.totalPrice += totalPrice
            } else {
                val newGroup = GroupedCartFood(foodInCart, 1, totalPrice)
                groupedFoodMap[foodName] = newGroup
            }
        }

        return groupedFoodMap.values.toList()
    }

    fun clearCart() {
        viewModelScope.launch {
            val cartValue = cartLiveData.value

            if (cartValue.isNullOrEmpty()) return@launch

            val deferredList = cartValue.map { food ->
                viewModelScope.async {
                    removeFoodFromCart(food, updateCart = false)
                }
            }

            deferredList.awaitAll()
        }
    }


}