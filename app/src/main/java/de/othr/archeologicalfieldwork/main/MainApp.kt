package de.othr.archeologicalfieldwork.main

import android.app.Application
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.UserJsonStore
import de.othr.archeologicalfieldwork.model.UserStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.atomic.AtomicLong

class MainApp : Application(), AnkoLogger {

    lateinit var userStore: UserStore
    var sites = ArrayList<Site>()
    var sitesCounter = AtomicLong()

    override fun onCreate() {
        super.onCreate()

        this.userStore = UserJsonStore(applicationContext)

        info("Application started")
    }
}