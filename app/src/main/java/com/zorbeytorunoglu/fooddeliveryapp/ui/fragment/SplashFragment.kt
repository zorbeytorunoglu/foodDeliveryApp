package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentSplashBinding
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment: Fragment() {

    private lateinit var binding: FragmentSplashBinding
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSplashBinding.inflate(inflater, container, false)

        lifecycleScope.launch {

            val direction = if (viewModel.currentUser == null)
                SplashFragmentDirections.actionSplashFragmentToLoginFragment()
            else
                SplashFragmentDirections.actionSplashFragmentToMainFragment()

            delay(3100)

            Navigation.findNavController(requireView()).navigate(direction)

        }

        return binding.root
    }

}