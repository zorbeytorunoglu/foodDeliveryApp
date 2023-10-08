package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods

import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.toFood
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.FoodListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor(
    private val repository: FoodRepository
) {

    operator fun invoke(scope: CoroutineScope) {

        flow<Result<List<Food>>> {

            try {
                emit(Result.Loading<List<Food>>())
                val foods = repository.getFoods().foods.map { it.toFood() }
                emit(Result.Success<List<Food>>(foods))
            } catch (e: HttpException) {
                emit(Result.Error<List<Food>>(e.localizedMessage ?: "An unexpected error occurred."))
            } catch (e: IOException) {
                emit(Result.Error<List<Food>>("Couldn't reach web service. Check your internet connection."))
            }

        }.onEach { result ->

            when (result) {
                is Result.Success -> {
                    repository.updateFoodListState(FoodListState(categories = getCategories(result.data) ?: emptyList()))
                }
                is Result.Error -> {
                    repository.updateFoodListState(FoodListState(error = result.message ?: "An unexpected error occurred."))
                }
                is Result.Loading -> {
                    repository.updateFoodListState(FoodListState(isLoading = true))
                }
            }

        }.launchIn(scope)

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

}