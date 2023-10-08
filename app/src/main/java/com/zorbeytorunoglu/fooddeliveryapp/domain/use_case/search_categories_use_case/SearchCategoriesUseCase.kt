package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.search_categories_use_case

import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.model.FoodListState
import javax.inject.Inject

class SearchCategoriesUseCase @Inject constructor(
    private val repository: FoodRepository
) {

    operator fun invoke(keyword: String): List<Category> {

        val filteredCategories = mutableListOf<Category>()

        for (category in repository.foodListState.value.categories) {
            val filteredFoodList = category.foodList.filter { food ->
                food.name.contains(keyword, ignoreCase = true)
            }

            if (filteredFoodList.isNotEmpty()) {
                val filteredCategory = Category(category.name, filteredFoodList)
                filteredCategories.add(filteredCategory)
            }
        }

        repository.updateFoodListState(FoodListState(categories = filteredCategories.toList()))

        return filteredCategories

    }

}