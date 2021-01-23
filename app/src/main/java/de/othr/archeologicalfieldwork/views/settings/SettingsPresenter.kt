package de.othr.archeologicalfieldwork.views.settings

import de.othr.archeologicalfieldwork.views.BasePresenter
import org.jetbrains.anko.AnkoLogger

class SettingsPresenter(view: SettingsView) : BasePresenter(view), AnkoLogger {

    private var settingsView: SettingsView = view

    fun getUserEmail() {
        settingsView?.setAccountEmailAddress(app.userStore.getCurrentUser()?.email)
    }

    fun getTotalNumOfSites() {
        settingsView?.setTotalNumberOfSites(app.siteStore.findAll().size)
    }

    fun getTotalNumOfVisitedSites() {
        settingsView?.setTotalNumberOfVisitedSites(0) //TODO
    }

    fun doApply() {
        val currentUser = app.userStore.getCurrentUser()
        val accountEmail = settingsView?.getAccountEmail()
        val accountPassword = settingsView.getAccountPassword()

        app.userStore.updateUser(currentUser?.id, accountEmail, accountPassword)
    }
}