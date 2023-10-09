package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.add_food_to_cart

import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.CRUDResponse
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart.GetFoodsInCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.GeneralState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddFoodToCartUseCase @Inject constructor(
    private val repository: FoodRepository,
    private val getFoodsInCartUseCase: GetFoodsInCartUseCase
) {

    operator fun invoke(food: Food, amount: Int, userUuid: String, updateCart: Boolean, scope: CoroutineScope) {
        flow<Result<CRUDResponse>> {

            try {
                emit(Result.Loading<CRUDResponse>())
                val result = repository.addFoodToCart(food, amount, userUuid)
                if (result.success == 0) {
                    emit(Result.Error<CRUDResponse>("Web service usage error occurred."))
                } else {
                    emit(Result.Success<CRUDResponse>(result))
                }
            } catch (e: HttpException) {
                emit(Result.Error<CRUDResponse>(e.localizedMessage ?: "An unexpected error occurred."))
            } catch (e: IOException) {
                emit(Result.Error<CRUDResponse>(e.localizedMessage ?: "Couldn't reach web service. Check your internet connection."))
            }

        }.onEach { result ->

            when (result) {
                is Result.Success -> {
                    repository.updateAddFoodToCartState(GeneralState(false, ""))
                    if (updateCart)
                        getFoodsInCartUseCase(scope)
                }
                is Result.Error -> {
                    repository.updateAddFoodToCartState(GeneralState(error = result.message ?: "An unexpected error occurred."))
                }
                is Result.Loading -> {
                    repository.updateAddFoodToCartState(GeneralState(isLoading = true))
                }
            }

        }.launchIn(scope)
    }

}