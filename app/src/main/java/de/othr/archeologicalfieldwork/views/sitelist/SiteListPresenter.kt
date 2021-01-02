package de.othr.archeologicalfieldwork.views.sitelist

import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.BaseView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error

class SiteListPresenter(view: BaseView) : BasePresenter(view), AnkoLogger {

    fun loadSites() {
        view?.showSites(app.sites)
    }

    fun openNewSiteActivity() {
        //TODO FEATURE/4
        error("Not yet implemented")
    }
}