package de.othr.archeologicalfieldwork.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.main.MainActivity
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Start actual app after 1 second
        Timer("Start App", false).schedule(1000) {
            startApp()
        }
    }

    private fun startApp() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}