package com.zorbeytorunoglu.fooddeliveryapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.databinding.CartFragmentCartCardDesignBinding
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart

class CartAdapter(
    private val context: Context,
    private val foodInCartList: List<FoodInCart>
): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CartFragmentCartCardDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartFragmentCartCardDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = foodInCartList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val foodInCart = foodInCartList[position]
        val binding = holder.binding

        binding.foodInCartNameTextView.text = foodInCart.food.name

        val totalSumOfPrices = foodInCartList.fold(0.0) { acc, item ->
            acc + item.food.price
        }

        binding.foodInCartTotalPriceTextView.text = "₺$totalSumOfPrices"

        Glide.with(context).load("${Constants.FULL_IMAGE_LOAD_URL}${foodInCart.food.imageName}").into(
            binding.foodInCartImageImageView
        )

    }

}