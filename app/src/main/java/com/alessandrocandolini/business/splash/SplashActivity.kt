package com.alessandrocandolini.business.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            screenContent()
        }
    }
}

@Composable
fun screenContent() {
    Column {
        Text("Hello")
        Text("world!")
    }
}