package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_foods_in_cart.GetFoodsInCartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartFragmentViewModel @Inject constructor(
    private val useCase: GetFoodsInCartUseCase
) : ViewModel() {

    fun getFoodsInCart() {



    }

}