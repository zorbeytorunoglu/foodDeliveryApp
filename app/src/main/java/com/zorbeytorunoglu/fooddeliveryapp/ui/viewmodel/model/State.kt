package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model

data class State<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String = ""
) {
}