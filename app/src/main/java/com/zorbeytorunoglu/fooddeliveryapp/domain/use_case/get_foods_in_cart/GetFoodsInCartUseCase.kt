package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart

import com.zorbeytorunoglu.fooddeliveryapp.common.Result
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_user.GetUserUseCase
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetFoodsInCartUseCase @Inject constructor(
    private val repository: FoodRepository,
    private val getUserUseCase: GetUserUseCase
) {

    operator fun invoke(scope: CoroutineScope) {

        if (repository.foodListState.value.categories.isEmpty()) return

        val foods = repository.foodListState.value.categories.flatMap { it.foodList }

        flow<Result<List<FoodInCart>>> {

            try {
                emit(Result.Loading<List<FoodInCart>>())
                val response = repository.getFoodsInCart(getUserUseCase().uid)
                if (response.success == 0) {
                    emit(Result.Error<List<FoodInCart>>("An error occurred on loading the foods in cart."))
                } else {

                    val foodInCartList = response.foods
                        .filter { responseFood -> foods.any { originalFood -> originalFood.name.equals(responseFood.name, true) } }
                        .map { responseFood ->
                            val originalFood = foods.find { originalFood -> originalFood.name.equals(responseFood.name, true) }
                            FoodInCart(responseFood.id.toInt(), originalFood!!)
                        }

                    emit(Result.Success<List<FoodInCart>>(foodInCartList))

                }
            } catch (e: HttpException) {
                emit(Result.Error<List<FoodInCart>>(e.localizedMessage ?: "An error occurred on getting the foods in cart."))
            } catch (e: IOException) {
                emit(Result.Error<List<FoodInCart>>(e.localizedMessage ?: "Couldn't reach web service. Check your internet connection."))
            }

        }.onEach { result ->

            when (result) {
                is Result.Success -> {
                    val data = result.data ?: emptyList()
                    repository.updateGetFoodsInCartState(State<List<FoodInCart>>(data = data))
                    repository.updateCartLiveData(data)
                }
                is Result.Error -> {
                    repository.updateGetFoodsInCartState(State<List<FoodInCart>>(error = result.message ?: "An error occurred when loading foods in cart."))
                    repository.updateCartLiveData(emptyList())
                }
                is Result.Loading -> {
                    repository.updateGetFoodsInCartState(State<List<FoodInCart>>(isLoading = true))
                }
            }

        }.launchIn(scope)

    }

}