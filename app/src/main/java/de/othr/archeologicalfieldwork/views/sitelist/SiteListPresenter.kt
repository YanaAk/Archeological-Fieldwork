package de.othr.archeologicalfieldwork.views.sitelist

import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.BaseView

class SiteListPresenter(view: BaseView) : BasePresenter(view) {

    fun loadSites() {
        view?.showSites(app.sites)
    }
}