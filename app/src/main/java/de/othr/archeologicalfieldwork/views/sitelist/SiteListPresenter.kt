package de.othr.archeologicalfieldwork.views.sitelist

import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.BaseView
import de.othr.archeologicalfieldwork.views.VIEW
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SiteListPresenter(view: BaseView) : BasePresenter(view), AnkoLogger {

    fun loadSites() {
        view?.showSites(app.siteStore.findAll())
    }

    fun openNewSiteActivity() {
        info("Open new Site activity")
        view?.navigateTo(VIEW.SITE)
    }

    fun doEditSite(site: Site) {
        view?.navigateTo(VIEW.SITE, 0, "site_edit", site)
    }

    fun openSettings() {
        view?.navigateTo(VIEW.SETTINGS)
    }
}