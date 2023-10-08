package com.zorbeytorunoglu.fooddeliveryapp.di

import com.google.firebase.auth.FirebaseAuth
import com.zorbeytorunoglu.fooddeliveryapp.common.Constants
import com.zorbeytorunoglu.fooddeliveryapp.data.remote.FoodApi
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.AuthRepository
import com.zorbeytorunoglu.fooddeliveryapp.data.repository.AuthRepositoryImpl
import com.zorbeytorunoglu.fooddeliveryapp.data.repository.FoodRepositoryImpl
import com.zorbeytorunoglu.fooddeliveryapp.domain.repository.FoodRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    @Singleton
    fun provideFoodApi(): FoodApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FoodApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodRepository(api: FoodApi): FoodRepository {
        return FoodRepositoryImpl(api)
    }

}