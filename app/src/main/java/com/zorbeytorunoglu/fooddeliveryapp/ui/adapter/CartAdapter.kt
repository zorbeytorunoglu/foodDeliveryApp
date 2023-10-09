package com.zorbeytorunoglu.fooddeliveryapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.databinding.CartFragmentCartCardDesignBinding
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.FoodInCart
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.GroupedCartFood
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.CartFragmentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CartAdapter(
    private val context: Context,
    private val groupedCartFoodList: MutableList<GroupedCartFood>,
    private val viewModel: CartFragmentViewModel,
    private val lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    interface CartAdapterListener {
        fun onCartEmpty()

        fun onFoodAdd(price: Double)

        fun onFoodRemove(price: Double)

    }

    private var listener: CartAdapterListener? = null

    fun setListener(listener: CartAdapterListener?) {
        this.listener = listener
    }

    inner class ViewHolder(val binding: CartFragmentCartCardDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartFragmentCartCardDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = groupedCartFoodList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val foodInCart = groupedCartFoodList[position]
        val binding = holder.binding

        binding.foodInCartNameTextView.text = foodInCart.foodInCart.food.name

        binding.foodInCartTotalPriceTextView.text = "₺${foodInCart.totalPrice}"
        binding.foodInCartAmountTextView.text = "${foodInCart.quantity}"

        Glide.with(context).load("${Constants.FULL_IMAGE_LOAD_URL}${foodInCart.foodInCart.food.imageName}").into(
            binding.foodInCartImageImageView
        )

        binding.foodInCartAddImageView.setOnClickListener {
            viewModel.addFoodToCart(foodInCart.foodInCart, true)
            listener?.onFoodAdd(foodInCart.foodInCart.food.price)
        }

        binding.foodInCartRemoveImageView.setOnClickListener {
            viewModel.removeFoodFromCart(foodInCart.foodInCart, true)
            listener?.onFoodRemove(foodInCart.foodInCart.food.price)
        }

        viewModel.cartLiveData.observe(lifecycleOwner) { foodInCartList ->

            if (foodInCartList.isNullOrEmpty()) {
                listener?.onCartEmpty()
                return@observe
            }

            val newGrouped = viewModel.groupAndCalculateTotal(foodInCartList)

            val newPosition = newGrouped.indexOfFirst { it.foodInCart.food.name.equals(foodInCart.foodInCart.food.name, true) }

            if (newPosition == -1) {
                val positionToRemove = groupedCartFoodList.indexOf(foodInCart)
                if (positionToRemove != -1) {
                    groupedCartFoodList.removeAt(positionToRemove)
                    notifyItemRemoved(positionToRemove)

                    if (groupedCartFoodList.isEmpty()) {
                        listener?.onCartEmpty()
                    }
                }
            } else {
                binding.foodInCartAmountTextView.text = newGrouped[newPosition].quantity.toString()
                binding.foodInCartTotalPriceTextView.text = "₺${newGrouped[newPosition].totalPrice}"
            }

        }

    }

    private suspend fun addDelayToView(view: View) {
        view.isClickable = false
        delay(1000)
        view.isClickable = true
    }

}