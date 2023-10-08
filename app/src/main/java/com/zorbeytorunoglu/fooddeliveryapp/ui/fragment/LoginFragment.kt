package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.zorbeytorunoglu.fooddeliveryapp.R
import com.zorbeytorunoglu.fooddeliveryapp.common.Resource
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentLoginBinding
import com.zorbeytorunoglu.fooddeliveryapp.ui.dialog.LoadingDialog
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val loadingDialog = LoadingDialog(requireContext(), layoutInflater)

        setSignupNavigationListeners(
            binding.signupFacebookCon,
            binding.signupMailIcon,
            binding.signupTwitterIcon
        )

        binding.button.setOnClickListener {

            val email = viewModel.getValidEmail(binding.textInputLayout, binding.emailEditText)
                ?: kotlin.run { return@setOnClickListener }

            val password = viewModel.getValidPassword(binding.textInputLayout2, binding.passwordEditText)
                ?: kotlin.run { return@setOnClickListener }

            viewModel.login(email, password)

        }

        viewModel.authResultLiveData.observe(viewLifecycleOwner) { result ->

            when (result) {
                is Resource.Success -> {
                    Navigation.findNavController(binding.root).navigate(
                        LoginFragmentDirections.actionLoginFragmentToMainFragment()
                    )
                }
                is Resource.Failure -> {
                    Snackbar.make(requireView(), "Incorrect password/email.", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) loadingDialog.show() else loadingDialog.dismiss()
        }

        return binding.root
    }

    private fun setSignupNavigationListeners(vararg imageViews: View) {
        imageViews.forEach { view ->
            view.setOnClickListener {
                Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

}