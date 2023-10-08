package com.zorbeytorunoglu.fooddeliveryapp.domain.model

import java.io.Serializable

data class Category(val name: String, val foodList: List<Food>): Serializable