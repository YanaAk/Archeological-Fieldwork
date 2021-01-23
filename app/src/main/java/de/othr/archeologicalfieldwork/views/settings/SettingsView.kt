package de.othr.archeologicalfieldwork.views.settings

import android.os.Bundle
import android.view.Menu
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.AnkoLogger

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
}