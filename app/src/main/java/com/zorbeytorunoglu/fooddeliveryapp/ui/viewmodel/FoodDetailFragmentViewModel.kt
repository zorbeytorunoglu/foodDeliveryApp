package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel

import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.GroupedCartFood
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.add_food_to_cart.AddFoodToCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_user.GetUserUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.remove_food_from_cart.RemoveFoodFromCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FoodDetailFragmentViewModel @Inject constructor(

    private val addFoodToCartUseCase: AddFoodToCartUseCase,
    private val removeFoodFromCartUseCase: RemoveFoodFromCartUseCase,
    private val userUseCase: GetUserUseCase,
    private val repository: FoodRepository

): ViewModel() {

    val cartLiveData = repository.cartLiveData

    fun addFoodToCart(food: Food) {
        addFoodToCartUseCase(food, 1, userUseCase().uid, true, viewModelScope)
    }

    fun removeFromCart(food: Food) {
        removeFoodFromCartUseCase(food, userUseCase().uid, true, viewModelScope)
    }

    fun updatedTotalPrice(): String {
        val data = cartLiveData.value

        if (data.isNullOrEmpty()) return "₺0"

        return "₺${groupAndCalculateTotal(data).sumOf { it.totalPrice }}"
    }

    private fun groupAndCalculateTotal(foodInCartList: List<FoodInCart>?): List<GroupedCartFood> {

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

    fun updatedFoodAmount(food: Food, foodList: List<FoodInCart>?): Int {
        if (foodList.isNullOrEmpty()) return 0

        return foodList.filter { it.food.name.equals(food.name, true) }.size
    }

}