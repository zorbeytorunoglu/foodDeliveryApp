package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods

import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.GetFoodsResponse
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.toFood
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetFoodsUseCase @Inject constructor(
    private val repository: FoodRepository
) {

    operator fun invoke(): Flow<Result<List<Food>>> = flow {

        try {
            emit(Result.Loading<List<Food>>())
            val foods = repository.getFoods().foods.map { it.toFood() }
            emit(Result.Success<List<Food>>(foods))
        } catch (e: HttpException) {
            emit(Result.Error<List<Food>>(e.localizedMessage ?: "An unexpected error occurred."))
        } catch (e: IOException) {
            emit(Result.Error<List<Food>>("Couldn't reach web service. Check your internet connection."))
        }

    }

}