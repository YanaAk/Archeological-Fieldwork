package de.othr.archeologicalfieldwork.views.sitelist

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import org.jetbrains.anko.AnkoLogger

class SiteListPresenter(private val siteListView: SiteListView) : BasePresenter(siteListView), AnkoLogger {

    private var favs: Boolean = false
    private var search: Boolean = false

    init {
        val args: SiteListViewArgs by siteListView.navArgs()

        favs = args.showFavs
    }

    fun loadSites() {
        if (favs) {
            val favoriteSiteIds = app.userStore.getCurrentUser()?.favoriteSites
            siteListView.setFavSelectionOn()
            view?.showSites(app.siteStore.resolveIds(favoriteSiteIds))
        } else {
            siteListView.setFavSelectionOff()
            view?.showSites(app.siteStore.findAll())
        }
    }

    fun openNewSiteActivity() {
        Navigation.findNavController(this.view?.requireView()!!).navigate(R.id.action_siteListView_to_siteView)
    }

    fun doEditSite(site: Site) {
        Navigation.findNavController(this.view?.requireView()!!).navigate(SiteListViewDirections.actionSiteListViewToSiteView(site))
    }

    fun openSettings() {
        Navigation.findNavController(this.view?.requireView()!!).navigate(R.id.action_siteListView_to_settingsView)
    }

    fun openMap() {
        Navigation.findNavController(this.view?.requireView()!!).navigate(R.id.action_siteListView_to_siteMapView)
    }

    fun toggleFavorites() {
        favs = !favs

        loadSites()
    }

    fun openLogin() {
        app.userStore.logout()
        Navigation.findNavController(this.view?.requireView()!!).navigate(R.id.action_siteListView_to_loginView)
    }

    fun toggleSearch() {
        search = !search

        if (!search) {
            siteListView.hideSearch()
            val imm = view!!.requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.requireView().windowToken, 0)
            loadSites()
        } else {
            siteListView.showSearch()
        }
    }

    fun doSearch(text: String) {
        view?.showSites(app.siteStore.searchForName(text))
    }
}