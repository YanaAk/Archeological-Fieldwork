package de.othr.archeologicalfieldwork.main

import android.app.Application
import de.othr.archeologicalfieldwork.model.Site
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.atomic.AtomicLong

class MainApp : Application(), AnkoLogger {

    var sites = ArrayList<Site>()
    var sitesCounter = AtomicLong()

    override fun onCreate() {
        super.onCreate()

        info("Application started")
    }
}