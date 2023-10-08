package com.zorbeytorunoglu.fooddeliveryapp.ui.viewmodel

import android.widget.EditText
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import com.zorbeytorunoglu.fooddeliveryapp.common.Resource
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {

    val authResultLiveData = MutableLiveData<Resource<FirebaseUser>>()

    val isLoading = MutableLiveData<Boolean>()

    val currentUser = repository.currentUser

    fun login(email: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.login(email, password)
            }
            isLoading.value = false
            authResultLiveData.value = result
        }
    }

    fun signup(name: String, email: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.signup(name, email, password)
            }
            isLoading.value = false
            authResultLiveData.value = result
        }
    }

    fun logout() {
        repository.logout()
    }

    fun getValidEmail(layout: TextInputLayout, editText: EditText): String? {

        if (editText.text.isNullOrBlank()) {
            layout.error = "Email can' be left empty."
            return null
        }

        val email = editText.text.toString()

        if (email.length <= 8) {
            layout.error = "Too short for an email."
            return null
        }

        if (!email.contains("@") || !email.contains(".")) {
            layout.error = "Incorrect email format."
            return null
        }

        return email

    }

    fun getValidUsername(layout: TextInputLayout, editText: EditText): String? {

        if (editText.text.isNullOrBlank()) {
            layout.error = "Username can't be left empty."
            return null
        }

        if (editText.text.toString().length < 5) {
            layout.error = "Username should be at least 5 characters."
            return null
        }

        if (editText.text.toString().length > 20) {
            layout.error = "Too long for an username."
            return null
        }

        if (!editText.text.toString().matches(Regex("[a-zA-Z0-9]+"))) {
            layout.error = "Only letters and numbers can be used."
            return null
        }

        return editText.text.toString()

    }

    fun getValidPassword(layout: TextInputLayout, editText: EditText): String? {

        if (editText.text.isNullOrBlank()) {
            layout.error = "Password can't be left empty."
            return null
        }

        val pass = editText.text.toString()

        if (pass.length < 8) {
            layout.error = "Too short for a password."
            return null
        }
        if (pass.isDigitsOnly()) {
            layout.error = "Password must contain letters."
            return null
        }

        return pass

    }

}