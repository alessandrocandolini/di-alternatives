package com.alessandrocandolini.di_alternatives.di

import android.content.Context
import com.alessandrocandolini.di_alternatives.BuildConfig
import com.alessandrocandolini.splash.ApiKeyInterceptor
import com.alessandrocandolini.splash.ApiKeyStore
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesApiKeyStore(): ApiKeyStore = ApiKeyStore { BuildConfig.API_KEY }

    @Singleton
    @Provides
    fun providesChuckerInterceptor(
        // @ActivityContext context: Context // why this compilation does not fail
        @ApplicationContext context: Context
    ): ChuckerInterceptor =
        ChuckerInterceptor.Builder(context)
            .alwaysReadResponseBody(true)
            .build()

    @Singleton
    @Provides
    fun providesOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(chuckerInterceptor)
            .build()

    @Singleton
    @Provides
    fun providesRetrofit(baseClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(Json {
                ignoreUnknownKeys = true
            }.asConverterFactory("application/json".toMediaType()))
            .client(baseClient)
            .baseUrl(baseUrl)
            .build()

    companion object {
        private const val baseUrl: String = "https://api.openweathermap.org/"
    }

}