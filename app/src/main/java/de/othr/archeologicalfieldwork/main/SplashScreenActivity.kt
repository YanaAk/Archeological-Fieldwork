package de.othr.archeologicalfieldwork.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.sitelist.SiteListView
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

    fun startApp() {
        startActivity(Intent(this, SiteListView::class.java))
        finish()
    }
}