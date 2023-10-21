package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentFoodDetailBinding
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.FoodDetailFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FoodDetailFragment : Fragment() {

    private lateinit var binding: FragmentFoodDetailBinding
    private val viewModel: FoodDetailFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)

        val bundle: FoodDetailFragmentArgs by navArgs()

        val food = bundle.food

        Glide.with(this)
            .load("${Constants.FULL_IMAGE_LOAD_URL}${food.imageName}")
            .into(binding.foodDetailImageImageView)

        binding.foodDetailFoodNameTextView.text = food.name

        binding.foodDetailAddCardView.setOnClickListener {
            viewModel.addFoodToCart(food)
        }

        binding.foodDetailRemoveCardView.setOnClickListener {
            viewModel.removeFromCart(food)
        }

        binding.backToMainImageView.setOnClickListener {
            Navigation.findNavController(it).navigate(
                FoodDetailFragmentDirections.actionFoodDetailFragmentToMainFragment()
            )
        }

        viewModel.cartLiveData.observe(viewLifecycleOwner) {

            binding.foodDetailFoodPriceTextView.text = viewModel.updatedTotalPrice()

            binding.foodDetailAmountTextView.text = viewModel.updatedFoodAmount(food, it).toString()

        }

        return binding.root
    }

}