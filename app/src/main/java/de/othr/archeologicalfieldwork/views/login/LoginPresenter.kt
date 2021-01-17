package de.othr.archeologicalfieldwork.views.login

import android.util.Patterns
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

    private fun isValidPassword(password: String): Boolean {
        return password.length > 5
    }

    private fun isValidUsername(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    fun checkInput(inputUsername: String, inputPassword: String) {
        var isValid = true

        if (inputUsername != null && !isValidUsername(inputUsername)) {
            loginView.setUsernameError()
            isValid = false
        }

        if (inputPassword != null && inputPassword.isNotBlank() && !isValidPassword(inputPassword)) {
            loginView.setPasswordError()
            isValid = false
        }

        loginView.setLoginButtonState(isValid)
    }
}