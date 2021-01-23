package de.othr.archeologicalfieldwork.views.settings

import de.othr.archeologicalfieldwork.helper.AccountInputStatus
import de.othr.archeologicalfieldwork.helper.checkAccountInput
import de.othr.archeologicalfieldwork.model.UserUpdateState
import de.othr.archeologicalfieldwork.views.BasePresenter
import org.jetbrains.anko.AnkoLogger

class SettingsPresenter(view: SettingsView) : BasePresenter(view), AnkoLogger {

    private var settingsView: SettingsView = view

    fun getUserEmail() {
        settingsView.setAccountEmailAddress(app.userStore.getCurrentUser()?.email)
    }

    fun getTotalNumOfSites() {
        settingsView.setTotalNumberOfSites(app.siteStore.findAll().size)
    }

    fun getTotalNumOfVisitedSites() {
        settingsView.setTotalNumberOfVisitedSites(0) //TODO feature/15
    }

    fun checkInput(inputUsername: String, inputPassword: String) {
        when(checkAccountInput(inputUsername, inputPassword)) {
            AccountInputStatus.VALID -> settingsView.setApplyButtonState(true)
            AccountInputStatus.INVALID_USERNAME -> {
                settingsView.setUsernameError()
                settingsView.setApplyButtonState(false)
            }
            AccountInputStatus.INVALID_PASSWORD -> {
                settingsView.setPasswordError()
                settingsView.setApplyButtonState(false)
            }
        }
    }

    fun doApply() {
        val currentUser = app.userStore.getCurrentUser()
        val accountEmail = settingsView.getAccountEmail()
        val accountPassword = settingsView.getAccountPassword()

        when(app.userStore.updateUser(currentUser?.id, accountEmail, accountPassword)) {
            UserUpdateState.FAILURE_USER_NOT_FOUND -> settingsView.showFailureMessage()
            UserUpdateState.SUCCESS -> settingsView.showSuccessMessage()
            UserUpdateState.FAILURE_USERNAME_USED -> settingsView.showUsernameTakenFailureMessage()
        }
    }
}