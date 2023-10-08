package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model

import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category

data class FoodListState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String = ""
)