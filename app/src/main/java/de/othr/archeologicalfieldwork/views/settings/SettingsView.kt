package de.othr.archeologicalfieldwork.views.settings

import android.os.Bundle
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView
import org.jetbrains.anko.AnkoLogger

class SettingsView : BaseView(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}