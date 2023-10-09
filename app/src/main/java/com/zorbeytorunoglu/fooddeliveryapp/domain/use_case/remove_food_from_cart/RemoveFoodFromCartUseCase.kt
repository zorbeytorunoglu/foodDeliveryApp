package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.remove_food_from_cart

import com.zorbeytorunoglu.fooddeliveryapp.data.remote.dto.CRUDResponse
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import kotlinx.coroutines.flow.flow
import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart.GetFoodsInCartUseCase
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.GeneralState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoveFoodFromCartUseCase @Inject constructor(
    private val getFoodsInCartUseCase: GetFoodsInCartUseCase,
    private val repository: FoodRepository
) {

    operator fun invoke(food: Food, userUuid: String, updateCart: Boolean, scope: CoroutineScope) {

        if (repository.cartLiveData.value == null) return
        if (repository.cartLiveData.value!!.isEmpty()) return

        val foodInCart = repository.cartLiveData.value!!.find { it.food.name.equals(food.name, true) } ?: return

        flow<Result<CRUDResponse>> {

            try {
                emit(Result.Loading<CRUDResponse>())
                val result = repository.removeFoodFromCart(foodInCart.foodCartId, userUuid)
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

        }.onEach { result ->

            when (result) {
                is Result.Loading -> {
                    repository.updateRemoveFoodFromCartState(GeneralState(isLoading = true))
                }
                is Result.Error -> {
                    repository.updateRemoveFoodFromCartState(GeneralState(error = result.message ?: "An unexpected error occurred on removing food from cart."))
                }
                is Result.Success -> {
                    repository.updateRemoveFoodFromCartState(GeneralState(false, ""))
                    if (updateCart)
                        getFoodsInCartUseCase(scope)
                }
            }

        }.launchIn(scope)

    }

}