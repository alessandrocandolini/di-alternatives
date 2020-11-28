package com.alessandrocandolini.di_alternatives.di

import android.util.Log
import com.alessandrocandolini.splash.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LoggingModule {

    @Provides
    fun providesLogger() : Logger = object : Logger {
        override fun debug(tag: String, message: String) {
            Log.d(tag, message)
        }

        override fun info(tag: String, message: String) {
            Log.i(tag, message)
        }

        override fun warn(tag : String, message: String) {
            Log.w(tag, message)
        }

        override fun error(tag: String, message: String, t: Throwable?) {
            Log.e(tag, message,t)
        }

    }

}