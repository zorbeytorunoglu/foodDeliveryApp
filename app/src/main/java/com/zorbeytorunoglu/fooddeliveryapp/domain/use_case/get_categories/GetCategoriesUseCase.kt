package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_categories

import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food

class GetCategoriesUseCase {

    fun getCategories(foodList: List<Food>): List<Category> {
        val categorizedFoods = foodList.groupBy { food ->
            when (food.id) {
                in arrayOf(1, 3, 7, 12) -> "Beverages"
                in arrayOf(4, 5, 8, 9, 10, 11) -> "Meals"
                in arrayOf(2, 6, 13, 14) -> "Desserts"
                else -> "Other"
            }
        }

        return categorizedFoods.map { (categoryName, foods) ->
            Category(categoryName, foods)
        }
    }

}