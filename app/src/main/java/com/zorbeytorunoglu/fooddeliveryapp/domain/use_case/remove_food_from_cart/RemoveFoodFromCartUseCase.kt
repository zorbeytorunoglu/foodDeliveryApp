package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.remove_food_from_cart

import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.CRUDResponse
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoveFoodFromCartUseCase @Inject constructor(
    private val repository: FoodRepository
) {

    fun removeFoodFromCart(foodInCartId: Int, userUuid: String):Flow<Result<CRUDResponse>> = flow {

        try {
            emit(Result.Loading<CRUDResponse>())
            val result = repository.removeFoodFromCart(foodInCartId, userUuid)
            if (result.success == 0) {
                emit(Result.Error<CRUDResponse>("Web service usage error occurred on removing food from cart."))
            } else {
                emit(Result.Success<CRUDResponse>(result))
            }

        } catch (e: HttpException) {
            emit(Result.Error<CRUDResponse>(e.localizedMessage ?: "An unexpected error occurred on removing food from cart."))
        } catch (e: IOException) {
            emit(Result.Error<CRUDResponse>(e.localizedMessage ?: "Couldn't reach web service. Check your internet connection."))
        }

    }

}