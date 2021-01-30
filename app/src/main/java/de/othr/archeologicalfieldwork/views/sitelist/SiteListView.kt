package de.othr.archeologicalfieldwork.views.sitelist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BaseView
import kotlinx.android.synthetic.main.activity_site_list.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SiteListView : BaseView(R.layout.activity_site_list), SiteListener, AnkoLogger {

    lateinit var presenter: SiteListPresenter
    lateinit var navView: NavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter(SiteListPresenter(this)) as SiteListPresenter

        setHasOptionsMenu(true)

        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        navView = requireActivity().findViewById(R.id.nav_view) as NavigationView
        val drawerLayout = requireActivity().findViewById(R.id.mainDrawerLayout) as DrawerLayout
        navView.setNavigationItemSelectedListener {dest ->
            when(dest.itemId) {
                R.id.item_logout -> presenter.openLogin()
                R.id.settingsView -> presenter.openSettings()
                R.id.siteMapView -> presenter.openMap()
                R.id.item_favs -> presenter.toggleFavorites()
            }

            drawerLayout.closeDrawers()

            true
        }

        hideSearch()

        searchButton.setOnClickListener { presenter.doSearch(searchText.text.toString()) }

        presenter.loadSites()
    }

    fun setFavSelectionOn() {
        navView.menu.findItem(R.id.item_favs).setIcon(android.R.drawable.btn_star_big_on)
    }

    fun setFavSelectionOff() {
        navView.menu.findItem(R.id.item_favs).setIcon(android.R.drawable.btn_star_big_off)
    }

    fun hideSearch() {
        searchText.isVisible = false
        searchText.setText("")
        searchButton.isVisible = false
    }

    fun showSearch() {
        searchText.isVisible = true
        searchButton.isVisible = true
    }

    override fun showSites(sites: List<Site>) {
        recyclerView.adapter = SiteAdapter(sites, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_site_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        info("triggered")
        when (item.itemId) {
            R.id.item_add -> presenter.openNewSiteActivity()
            R.id.search -> presenter.toggleSearch()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSiteClick(site: Site) {
        info("Site $site clicked")
        presenter.doEditSite(site)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.loadSites()
        super.onActivityResult(requestCode, resultCode, data)
    }
}
