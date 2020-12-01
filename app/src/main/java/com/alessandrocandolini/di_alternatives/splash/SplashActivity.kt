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
            // kotlin does not support call by name semantics, so we still need this IF to wrap the log.
            // if you know you are going to use R8 in release (like in this project does) then this IF is not needed, R8 can be used to strip the logs during compilation
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