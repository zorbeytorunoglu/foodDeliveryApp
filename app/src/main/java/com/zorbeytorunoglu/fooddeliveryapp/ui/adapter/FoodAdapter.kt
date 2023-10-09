package com.zorbeytorunoglu.fooddeliveryapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.databinding.MainFragmentFoodCardDesignBinding
import com.zorbeytorunoglu.fooddeliveryapp.domain.model.Food
import com.zorbeytorunoglu.fooddeliveryapp.ui.fragment.MainFragmentDirections
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.MainFragmentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FoodAdapter(
    private val context: Context,
    private val foodList: List<Food>,
    private val glide: RequestManager,
    private val viewModel: MainFragmentViewModel,
    private val lifecycleOwner: LifecycleOwner
): RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MainFragmentFoodCardDesignBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            MainFragmentFoodCardDesignBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun getItemCount(): Int = foodList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val food = foodList[position]
        val binding = holder.binding

        binding.foodName.text = food.name
        binding.foodPrice.text = "₺${food.price}"

        binding.amountTextView.visibility = View.GONE
        binding.removeImageView.visibility = View.GONE

        glide
            .load("${Constants.FULL_IMAGE_LOAD_URL}${food.imageName}")
            .into(binding.foodImage)

        viewModel.cartLiveData.observe(lifecycleOwner) { liveData ->

            if (liveData == null || liveData.isEmpty() || liveData.none { it.food.name.equals(food.name, true) }) {
                clearNHideControlView(binding.amountTextView, binding.removeImageView)
            } else {
                val count = liveData.count { it.food.name.equals(food.name, true) }
                binding.amountTextView.visibility = View.VISIBLE
                binding.amountTextView.text = count.toString()
                binding.removeImageView.visibility = View.VISIBLE
            }

        }

        binding.addImageView.setOnClickListener {
            viewModel.addFoodToCart(food, 1)
            delayClickable(binding.addImageView, 1000)
        }

        binding.removeImageView.setOnClickListener {
            viewModel.removeFoodFromCart(food)
            delayClickable(binding.removeImageView, 1000)
        }

        binding.foodImage.setOnClickListener {
            Navigation.findNavController(it).navigate(
                MainFragmentDirections.actionMainFragmentToFoodDetailFragment(food)
            )
        }

    }

    private fun clearNHideControlView(vararg views: View) {
        views.forEach { view ->
            if (view is TextView) {
                view.text = "0"
            }
            view.visibility = View.GONE
        }
    }

    private fun delayClickable(view: View, delay: Long) {
        viewModel.viewModelScope.launch {
            view.isClickable = false
            delay(delay)
            view.isClickable = true
        }
    }

}