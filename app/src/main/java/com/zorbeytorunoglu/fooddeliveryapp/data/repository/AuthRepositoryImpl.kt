package com.zorbeytorunoglu.fooddeliveryapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.zorbeytorunoglu.fooddeliveryapp.common.Resource
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = result?.user
                if (user != null) {
                    Resource.Success(user)
                } else {
                    Resource.Failure(IOException("User is null."))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Failure(e)
            }
        }
    }

    override suspend fun signup(name: String, email: String, password: String): Resource<FirebaseUser> {
        return withContext(Dispatchers.IO) {
            try {
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                result?.user?.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(name).build())
                    ?.await()

                val user = result?.user
                if (user != null) {
                    Resource.Success(user)
                } else {
                    Resource.Failure(IOException("User is null"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Resource.Failure(e)
            }
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

}