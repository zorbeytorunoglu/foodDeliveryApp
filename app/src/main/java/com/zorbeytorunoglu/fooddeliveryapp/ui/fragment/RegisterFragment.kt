package com.zorbeytorunoglu.fooddeliveryapp.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.zorbeytorunoglu.fooddeliveryapp.R
import com.zorbeytorunoglu.fooddeliveryapp.common.Resource
import com.zorbeytorunoglu.fooddeliveryapp.databinding.FragmentRegisterBinding
import com.zorbeytorunoglu.fooddeliveryapp.ui.dialog.LoadingDialog
import com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels<AuthViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        val loadingDialog = LoadingDialog(requireContext(), layoutInflater)

        binding.alreadyHaveAccountTV.setOnClickListener {
            Navigation.findNavController(it).navigate(
                RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            )
        }

        binding.signupButton.setOnClickListener { _ ->

            val username = viewModel.getValidUsername(binding.textInputLayout3, binding.usernameEditText)
                ?: kotlin.run { return@setOnClickListener }

            val email = viewModel.getValidEmail(binding.textInputLayout4, binding.emailEditText)
                ?: kotlin.run { return@setOnClickListener }

            val password = viewModel.getValidPassword(binding.textInputLayout5, binding.passwordEditText)
                ?: kotlin.run { return@setOnClickListener }

            viewModel.signup(username, email, password)

        }

        viewModel.authResultLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Snackbar.make(requireView(), "Incorrect password/email.", Snackbar.LENGTH_SHORT).show()
                    Log.e("Authentication Failure", it.exception.message.toString())
                }
                is Resource.Success -> {
                    Navigation.findNavController(binding.root).navigate(
                        RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
                    )
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) loadingDialog.show() else loadingDialog.dismiss()
        }

        return binding.root
    }

}