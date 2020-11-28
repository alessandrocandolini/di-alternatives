package com.alessandrocandolini.di_alternatives.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import com.alessandrocandolini.di_alternatives.BuildConfig
import com.alessandrocandolini.splash.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var logger : Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            // kotlin does not support call by name semantics, so we still need the IF debug
            // if you know you are going to use R8 (like in this project) then this "if" is not needed
            logger.info("SplashActivity", "onCreate")
        }
        setContent {
            screenContent("")
        }
    }
}

@Composable
fun screenContent(s : String) {
    Column {
        Text("Hello")
        Text("world!")
    }
}