package de.othr.archeologicalfieldwork.main

import android.app.Application
import com.google.firebase.FirebaseApp
import de.othr.archeologicalfieldwork.model.SiteStore
import de.othr.archeologicalfieldwork.model.UserStore
import de.othr.archeologicalfieldwork.model.firebase.FirebaseSiteStore
import de.othr.archeologicalfieldwork.model.firebase.FirebaseUserStore
import de.othr.archeologicalfieldwork.model.json.SiteJsonStore
import de.othr.archeologicalfieldwork.model.json.UserJsonStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var userStore: UserStore
    lateinit var siteStore: SiteStore

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        goOnline()

        info("Application started")
    }

    fun goOffline() {
        this.userStore = UserJsonStore(applicationContext)
        this.siteStore = SiteJsonStore(applicationContext)
        info("Gone offline")
    }

    fun goOnline() {
        this.userStore = FirebaseUserStore()
        this.siteStore = FirebaseSiteStore(applicationContext)
        info("Gone online")
    }
}