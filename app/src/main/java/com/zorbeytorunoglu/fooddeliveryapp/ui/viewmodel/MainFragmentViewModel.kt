package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.add_food_to_cart.AddFoodToCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods.GetFoodsUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart.GetFoodsInCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_user.GetUserUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.image_preload_use_case.ImagePreloadUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.remove_food_from_cart.RemoveFoodFromCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.search_categories_use_case.SearchCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val addFoodToCartUseCase: AddFoodToCartUseCase,
    private val removeFoodFromCartUseCase: RemoveFoodFromCartUseCase,
    private val getFoodsInCartUseCase: GetFoodsInCartUseCase,
    private val searchCategoriesUseCase: SearchCategoriesUseCase,
    private val imagePreloadUseCase: ImagePreloadUseCase
): ViewModel() {

    init {
        getFoods()
    }

    fun removeFoodFromCart(food: Food) {
        removeFoodFromCartUseCase(food,getUserUseCase().uid,viewModelScope)
    }

    fun addFoodToCart(food: Food, amount: Int) {
        addFoodToCartUseCase.addFoodToCart(food,amount,getUserUseCase().uid,viewModelScope)
    }

    fun getFoodsInCart() {
        getFoodsInCartUseCase(viewModelScope)
    }

    fun getFoods() {
        getFoodsUseCase(viewModelScope)
    }

    fun searchCategories(keyword: String): List<Category> = searchCategoriesUseCase(keyword)

    suspend fun waitForImagePreloadingCompletion(categories: List<Category>, glide: RequestManager) {
        imagePreloadUseCase(categories, glide)
    }

}