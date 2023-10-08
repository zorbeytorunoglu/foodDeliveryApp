package com.zorbeytorunoglu.fooddeliveryapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.RequestManager
import com.zorbeytorunoglu.fooddeliveryapp.databinding.MainFragmentCategoryCardDesignBinding
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Category
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.MainFragmentViewModel

class CategoryAdapter(
    private val context: Context,
    private val categoryList: List<Category>,
    private val glide: RequestManager,
    private val viewModel: MainFragmentViewModel,
    private val lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MainFragmentCategoryCardDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MainFragmentCategoryCardDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val category = categoryList[position]
        val binding = holder.binding

        binding.categoryName.text = category.name

        binding.foodRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        binding.foodRecyclerView.adapter = FoodAdapter(context, category.foodList, glide, viewModel, lifecycleOwner)

    }


}