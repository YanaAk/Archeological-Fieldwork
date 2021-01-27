package de.othr.archeologicalfieldwork.views.login

import de.othr.archeologicalfieldwork.helper.AccountInputStatus
import de.othr.archeologicalfieldwork.helper.checkAccountInput
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult
import de.othr.archeologicalfieldwork.views.VIEW
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LoginPresenter(view: LoginView) : BasePresenter(view) {

    private val loginView: LoginView = view

    init {
        app = view.application as MainApp
    }

    fun doLoginOrSignup(email: String, password: String) {
        doAsync {
            app.userStore.doesUserExist(email, object : ProgressableForResult<Boolean, Void> {
                override fun start() {
                    uiThread {
                        loginView.showProgressBar()
                    }
                }

                override fun done(r: Boolean) {
                    if (r) {
                        // do login
                        app.userStore.login(email, password, object : Progressable {
                            override fun start() {}

                            override fun done() {
                                uiThread {
                                    navigateToStartPage()
                                    loginView.hideProgressBar()
                                }
                            }

                            override fun failure() {
                                uiThread {
                                    loginView.setLoginError()
                                }
                            }
                        })
                    } else {
                        // do signup
                        app.userStore.signup(email, password, object : Progressable {
                            override fun start() {}

                            override fun done() {
                                uiThread {
                                    navigateToStartPage()
                                    loginView.hideProgressBar()
                                }
                            }

                            override fun failure() {
                                uiThread {
                                    loginView.setSignupError()
                                    loginView.hideProgressBar()
                                }
                            }
                        })
                    }
                }

                override fun failure(r: Void) {
                    uiThread {
                        loginView.setLoginError()
                        loginView.hideProgressBar()
                    }
                }
            })
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