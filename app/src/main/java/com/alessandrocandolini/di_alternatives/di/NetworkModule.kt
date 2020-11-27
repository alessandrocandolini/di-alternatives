package com.alessandrocandolini.di_alternatives.di

import com.alessandrocandolini.splash.ApiKeyInterceptor
import com.alessandrocandolini.splash.ApiKeyStore
import com.alessandrocandolini.di_alternatives.BuildConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesApiKeyStore() : ApiKeyStore = ApiKeyStore { BuildConfig.API_KEY }

    @Provides
    fun providesOkHttpClient(
        apiKeyInterceptor : ApiKeyInterceptor
    ) : OkHttpClient  =
        OkHttpClient.Builder().addInterceptor(apiKeyInterceptor).build()

    @Provides
    fun providesRetrofit(baseClient : OkHttpClient) : Retrofit =
        Retrofit.Builder()
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType()))
            .client(baseClient)
            .baseUrl(baseUrl)
            .build()

    companion object {
        private const val baseUrl : String = "https://api.openweathermap.org/"
    }

}