package de.othr.archeologicalfieldwork.views.login

import de.othr.archeologicalfieldwork.helper.AccountInputStatus
import de.othr.archeologicalfieldwork.helper.checkAccountInput
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.VIEW

class LoginPresenter(view: LoginView) : BasePresenter(view) {

    private val loginView: LoginView = view

    init {
        app = view.application as MainApp
    }

    fun doLoginOrSignup(email: String, password: String) {
        if (app.userStore.doesUserExist(email)) {
            // do login
            if (app.userStore.login(email, password)) {
                navigateToStartPage()
            } else {
                loginView.setLoginError()
            }
        } else {
            // do signup
            if (app.userStore.signup(email, password)) {
                navigateToStartPage()
            } else {
                loginView.setSignupError()
            }
        }
    }

    private fun navigateToStartPage() {
        view?.navigateTo(VIEW.START)
    }

    fun checkInput(inputUsername: String, inputPassword: String) {
        when(checkAccountInput(inputUsername, inputPassword)) {
            AccountInputStatus.VALID -> loginView.setLoginButtonState(true)
            AccountInputStatus.INVALID_USERNAME -> {
                loginView.setUsernameError()
                loginView.setLoginButtonState(false)
            }
            AccountInputStatus.INVALID_PASSWORD -> {
                loginView.setPasswordError()
                loginView.setLoginButtonState(false)
            }
        }
    }
}