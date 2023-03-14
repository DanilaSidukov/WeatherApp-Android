package com.sidukov.weatherapp.ui.common

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sidukov.weatherapp.ui.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 31) installSplashScreen()
        startActivity(
            Intent( this, MainActivity::class.java )
        )
        finish()
    }

}