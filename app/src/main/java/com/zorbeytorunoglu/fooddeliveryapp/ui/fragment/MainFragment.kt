package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentMainBinding
import com.zorbeytorunoglu.fooddeliveryapp.ui.adapter.CategoryAdapter
import com.zorbeytorunoglu.fooddeliveryapp.ui.dialog.LoadingDialog
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.MainFragmentViewModel
import com.zorbeytorunoglu.fooddeliveryapp.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels<MainFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater, container, false)

        val loadingDialog = LoadingDialog(requireContext(), layoutInflater)

        ViewCompat.setNestedScrollingEnabled(binding.categoryRecyclerView, false)
        binding.cartSizeCardView.visibility = View.GONE

        binding.categoryRecyclerView.layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)

        lifecycleScope.launch {
            viewModel.foodListState.collectLatest {

                loadingDialog.show()

                if (it.error.isNotBlank()) {
                    showSnackbar(it.error)
                    return@collectLatest
                }
                if (!it.isLoading) {
                    val glide = Glide.with(this@MainFragment)
                    viewModel.waitForImagePreloadingCompletion(it.categories, glide)

                    binding.categoryRecyclerView.adapter =
                        CategoryAdapter(requireContext(), it.categories, glide, viewModel, viewLifecycleOwner)
                    loadingDialog.dismiss()
                    viewModel.getFoodsInCart()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.addFoodToCartState.collectLatest {
                if (it.error.isNotBlank()) {
                    showSnackbar(it.error)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.cartLiveData.observe(viewLifecycleOwner) { foodInCartList ->

                val size = foodInCartList.size

                binding.cartSizeCardView.visibility = if (size > 0) View.VISIBLE else View.GONE
                binding.cartItemSizeTextView.text = size.toString()

            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(keyword: String?): Boolean {
                if (keyword.isNullOrBlank()) {
                    viewModel.getFoods()
                } else {
                    viewModel.searchCategories(keyword)
                }
                return true
            }

            override fun onQueryTextChange(keyword: String?): Boolean {
                if (keyword.isNullOrBlank()) {
                    viewModel.getFoods()
                } else {
                    viewModel.searchCategories(keyword)
                }
                return false
            }

        })

        binding.cartImageView.setOnClickListener {

            Navigation.findNavController(it).navigate(MainFragmentDirections.actionMainFragmentToCartFragment())

        }

        return binding.root
    }

}