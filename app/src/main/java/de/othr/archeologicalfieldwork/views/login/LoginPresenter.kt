package de.othr.archeologicalfieldwork.views.login

import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.helper.AccountInputStatus
import de.othr.archeologicalfieldwork.helper.checkAccountInput
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class LoginPresenter(view: LoginView) : BasePresenter(view) {

    private var navView: NavigationView? = null
    private val loginView: LoginView = view

    init {
        app = view.requireActivity().application as MainApp
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
                                    loginView.hideProgressBar()
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
        restoreDrawer()
        Navigation.findNavController(this.view?.requireView()!!).navigate(R.id.action_loginView_to_siteListView)
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

    fun prepareDrawer(navView: NavigationView) {
        this.navView = navView
        navView.menu.findItem(R.id.settingsView).isVisible = false
        navView.menu.findItem(R.id.item_favs).isVisible = false
        navView.menu.findItem(R.id.siteMapView).isVisible = false
        navView.menu.findItem(R.id.item_logout).isVisible = false
        navView.menu.findItem(R.id.offlineToggleButton).isVisible = true
    }

    fun restoreDrawer() {
        navView?.menu!!.findItem(R.id.settingsView).isVisible = true
        navView?.menu!!.findItem(R.id.item_favs).isVisible = true
        navView?.menu!!.findItem(R.id.siteMapView).isVisible = true
        navView?.menu!!.findItem(R.id.item_logout).isVisible = true
        navView?.menu!!.findItem(R.id.offlineToggleButton).isVisible = false
    }

    fun switchOffline(checked: Boolean) {
        if (checked) {
            app.goOffline()
        } else {
            app.goOnline()
        }
    }
}