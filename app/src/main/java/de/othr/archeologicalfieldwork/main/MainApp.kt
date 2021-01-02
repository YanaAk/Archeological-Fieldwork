package de.othr.archeologicalfieldwork.main

import android.app.Application
import de.othr.archeologicalfieldwork.model.Site
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    var sites = ArrayList<Site>()

    override fun onCreate() {
        super.onCreate()

        info("Application started")
    }
}