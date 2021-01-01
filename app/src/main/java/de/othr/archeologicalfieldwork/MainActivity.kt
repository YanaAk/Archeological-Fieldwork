package de.othr.archeologicalfieldwork

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import de.othr.archeologicalfieldwork.model.Site

class MainActivity : AppCompatActivity() {

    var sites = ArrayList<Site>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}