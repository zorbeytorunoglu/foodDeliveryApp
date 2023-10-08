package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.add_food_to_cart.AddFoodToCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods.GetFoodsUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart.GetFoodsInCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_user.GetUserUseCase
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.remove_food_from_cart.RemoveFoodFromCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.FoodListState
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.GeneralState
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val getFoodsUseCase: GetFoodsUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val addFoodToCartUseCase: AddFoodToCartUseCase,
    private val removeFoodFromCartUseCase: RemoveFoodFromCartUseCase,
    private val getFoodsInCartUseCase: GetFoodsInCartUseCase,
    private val repository: FoodRepository
): ViewModel() {

    private val _state = MutableStateFlow(FoodListState())
    val state: StateFlow<FoodListState> = _state

    private val _cartLiveData = MutableLiveData<List<FoodInCart>>()
    val cartLiveData: LiveData<List<FoodInCart>> = _cartLiveData

    private val _addFoodToCartState = MutableStateFlow(GeneralState())
    val addFoodToCartState: StateFlow<GeneralState> = _addFoodToCartState

    private val _removeFoodFromCartState = MutableStateFlow(GeneralState())
    val removeFoodFromCartState: StateFlow<GeneralState> = _removeFoodFromCartState

    private val _getFoodsInCartState = MutableStateFlow(State<List<FoodInCart>>())
    val getFoodsInCartState: StateFlow<State<List<FoodInCart>>> = _getFoodsInCartState


    init {
        getFoods()
        Log.e("UUID", "Senin UUID: ${getUserUseCase().uid}")
    }

    fun removeFoodFromCart(food: Food) {

        if (_cartLiveData.value == null) return
        if (_cartLiveData.value!!.isEmpty()) return

        val foodInCart = _cartLiveData.value!!.find { it.food.name.equals(food.name, true) } ?: return

        removeFoodFromCartUseCase.removeFoodFromCart(foodInCart.foodCartId, getUserUseCase().uid).onEach { result ->

            when (result) {
                is Result.Loading -> {
                    _removeFoodFromCartState.value = GeneralState(isLoading = true)
                }
                is Result.Error -> {
                    _removeFoodFromCartState.value = GeneralState(error = result.message ?: "An unexpected error occurred on removing food from cart.")
                }
                is Result.Success -> {
                    _removeFoodFromCartState.value = GeneralState(false, "")
                    getFoodsInCart()
                }
            }

        }.launchIn(viewModelScope)
    }

    fun addFoodToCart(food: Food, amount: Int) {
        addFoodToCartUseCase.addFoodToCart(food,amount,getUserUseCase().uid,viewModelScope)
    }

    fun getFoodsInCart() {
        if (_state.value.categories.isEmpty()) return
        val foods = _state.value.categories.flatMap { it.foodList }
        getFoodsInCartUseCase.getFoodsInCart(foods,getUserUseCase().uid,viewModelScope)
    }



    fun getFoods() {

        getFoodsUseCase().onEach { result ->

            when (result) {
                is Result.Success -> {
                    _state.value = FoodListState(categories = getCategories(result.data) ?: emptyList())
                }
                is Result.Error -> {
                    _state.value = FoodListState(error = result.message ?: "An unexpected error occurred.")
                }
                is Result.Loading -> {
                    _state.value = FoodListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

    fun searchCategories(keyword: String): List<Category> {
        val filteredCategories = mutableListOf<Category>()

        for (category in state.value.categories) {
            val filteredFoodList = category.foodList.filter { food ->
                food.name.contains(keyword, ignoreCase = true)
            }

            if (filteredFoodList.isNotEmpty()) {
                val filteredCategory = Category(category.name, filteredFoodList)
                filteredCategories.add(filteredCategory)
            }
        }

        _state.value = FoodListState(categories = filteredCategories.toList())

        return filteredCategories
    }


    private fun getCategories(foodList: List<Food>?): List<Category>? {
        if (foodList == null) return null
        val categorizedFoods = foodList.groupBy { food ->
            when (food.id) {
                in arrayOf(1, 3, 7, 12) -> "Beverages"
                in arrayOf(4, 5, 8, 9, 10, 11) -> "Meals"
                in arrayOf(2, 6, 13, 14) -> "Desserts"
                else -> "Other"
            }
        }

        return categorizedFoods.map { (categoryName, foods) ->
            Category(categoryName, foods)
        }
    }

    suspend fun waitForImagePreloadingCompletion(categories: List<Category>, glide: RequestManager) {
        val deferredPreloads = mutableListOf<CompletableDeferred<Unit>>()

        categories.forEach {
            it.foodList.forEach { food ->
                val deferred = CompletableDeferred<Unit>()

                withContext(Dispatchers.IO) {
                    glide.load("${Constants.BASE_URL}${Constants.API_URL}${Constants.GET_FOOD_IMAGE_URL}${food.imageName}")
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                deferred.completeExceptionally(IOException("Image could not be loaded. ${e?.localizedMessage}"))
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable,
                                model: Any,
                                target: Target<Drawable?>?,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                deferred.complete(Unit)
                                return true
                            }
                        })
                        .preload()
                }

                deferredPreloads.add(deferred)
            }

        }

        deferredPreloads.awaitAll()

    }

}