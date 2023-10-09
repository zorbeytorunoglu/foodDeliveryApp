package com.zorbeytorunoglu.fooddeliveryapp.domain.model

data class GroupedCartFood(
    val foodInCart: FoodInCart,
    var quantity: Int,
    var totalPrice: Double
) {
}