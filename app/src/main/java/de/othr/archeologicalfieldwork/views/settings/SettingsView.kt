package de.othr.archeologicalfieldwork.views.settings

import android.os.Bundle
import android.view.Menu
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView
import de.othr.archeologicalfieldwork.views.login.afterTextChanged
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast

class SettingsView : BaseView(), AnkoLogger {

    lateinit var presenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        presenter = initPresenter (SettingsPresenter(this)) as SettingsPresenter

        init(settingsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.getUserEmail()
        presenter.getTotalNumOfSites()
        presenter.getTotalNumOfVisitedSites()

        settingsUsername.afterTextChanged {
            checkInput()
        }

        settingsPassword.afterTextChanged {
            checkInput()
        }

        settingsApply.setOnClickListener { presenter.doApply() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_blank, menu)

        return super.onCreateOptionsMenu(menu)
    }

    fun setTotalNumberOfSites(size: Int) {
        settingsStatsSitesNum.text = size.toString()
    }

    fun setTotalNumberOfVisitedSites(size: Int) {
        settingsStatsVisitedNum.text = size.toString()
    }

    fun setAccountEmailAddress(email: String?) {
        settingsUsername.setText(email?.orEmpty())
    }

    fun getAccountEmail(): String {
        return settingsUsername.text.toString()
    }

    fun getAccountPassword(): String {
        return settingsPassword.text.toString()
    }

    private fun checkInput() {
        presenter.checkInput(settingsUsername.text.toString(), settingsPassword.text.toString())
    }

    fun setUsernameError() {
        settingsUsername.error = getString(R.string.invalid_email)
    }

    fun setPasswordError() {
        settingsPassword.error = getString(R.string.invalid_password)
    }

    fun showSuccessMessage() {
        toast(R.string.account_data_changed)
    }

    fun showFailureMessage() {
        toast(R.string.account_change_error)
    }

    fun setApplyButtonState(state: Boolean) {
        settingsApply.isEnabled = state
    }

    fun showUsernameTakenFailureMessage() {
        toast(R.string.account_username_taken)
    }
}