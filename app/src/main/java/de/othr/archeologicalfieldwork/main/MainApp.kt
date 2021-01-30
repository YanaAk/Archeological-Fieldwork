package de.othr.archeologicalfieldwork.main

import android.app.Application
import com.google.firebase.FirebaseApp
import de.othr.archeologicalfieldwork.model.SiteStore
import de.othr.archeologicalfieldwork.model.UserStore
import de.othr.archeologicalfieldwork.model.firebase.FirebaseSiteStore
import de.othr.archeologicalfieldwork.model.firebase.FirebaseUserStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var userStore: UserStore
    lateinit var siteStore: SiteStore

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        this.userStore = FirebaseUserStore()
        this.siteStore = FirebaseSiteStore(applicationContext)

        info("Application started")
    }
}