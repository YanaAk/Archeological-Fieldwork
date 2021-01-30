package de.othr.archeologicalfieldwork.views.settings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView
import de.othr.archeologicalfieldwork.views.login.afterTextChanged
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.AnkoLogger

class SettingsView : BaseView(R.layout.activity_settings), AnkoLogger {

    lateinit var presenter: SettingsPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter (SettingsPresenter(this)) as SettingsPresenter

        setHasOptionsMenu(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_blank, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun setTotalNumberOfSites(size: Int) {
        settingsStatsSitesNum.text = size.toString()
    }

    fun setTotalNumberOfVisitedSites(size: Int) {
        settingsStatsVisitedNum.text = size.toString()
    }

    fun setAccountEmailAddress(email: String?) {
        settingsUsername.setText(email.orEmpty())
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
        Toast.makeText(activity, R.string.account_data_changed, Toast.LENGTH_SHORT).show()
    }

    fun showFailureMessage() {
        Toast.makeText(activity, R.string.account_change_error, Toast.LENGTH_SHORT).show()
    }

    fun setApplyButtonState(state: Boolean) {
        settingsApply.isEnabled = state
    }

    fun showUsernameTakenFailureMessage() {
        Toast.makeText(activity, R.string.account_username_taken, Toast.LENGTH_SHORT).show()
    }

    fun showWrongPassword() {
        Toast.makeText(activity, R.string.invalid_password__wrong_password, Toast.LENGTH_SHORT).show()
    }
}