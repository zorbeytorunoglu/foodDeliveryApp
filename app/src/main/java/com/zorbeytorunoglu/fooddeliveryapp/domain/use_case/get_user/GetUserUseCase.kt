package com.zorbeytorunoglu.fooddeliveryapp.domain.use_case.get_user

import com.google.firebase.auth.FirebaseUser
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.AuthRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    operator fun invoke(): FirebaseUser = authRepository.currentUser!!

}