package de.othr.archeologicalfieldwork.views.settings

import de.othr.archeologicalfieldwork.helper.AccountInputStatus
import de.othr.archeologicalfieldwork.helper.checkAccountInput
import de.othr.archeologicalfieldwork.model.UserUpdateState
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.ProgressableForResult
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
        app.userStore.getCurrentUser()?.visitedSites?.let {
            settingsView.setTotalNumberOfVisitedSites(it.size)
        }
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

        app.userStore.updateUser(currentUser?.id, accountEmail, accountPassword, object: ProgressableForResult<UserUpdateState, UserUpdateState> {
            override fun start() {}

            override fun done(r: UserUpdateState) {
                when(r) {
                    UserUpdateState.FAILURE_USER_NOT_FOUND -> settingsView.showFailureMessage()
                    UserUpdateState.SUCCESS -> settingsView.showSuccessMessage()
                    UserUpdateState.FAILURE_USERNAME_USED -> settingsView.showUsernameTakenFailureMessage()
                    UserUpdateState.FAILURE_PASSWORD_ERROR -> settingsView.showFailureMessage()
                    UserUpdateState.FAILURE_WRONG_PASSWORD -> settingsView.showWrongPassword()
                }
            }

            override fun failure(r: UserUpdateState) {
                when(r) {
                    UserUpdateState.FAILURE_USER_NOT_FOUND -> settingsView.showFailureMessage()
                    UserUpdateState.SUCCESS -> settingsView.showSuccessMessage()
                    UserUpdateState.FAILURE_USERNAME_USED -> settingsView.showUsernameTakenFailureMessage()
                    UserUpdateState.FAILURE_PASSWORD_ERROR -> settingsView.showFailureMessage()
                    UserUpdateState.FAILURE_WRONG_PASSWORD -> settingsView.showWrongPassword()
                }
            }
        })
    }
}