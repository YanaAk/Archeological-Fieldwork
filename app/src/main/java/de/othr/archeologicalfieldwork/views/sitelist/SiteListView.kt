package de.othr.archeologicalfieldwork.views.sitelist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BaseView
import kotlinx.android.synthetic.main.activity_site_list_view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SiteListView : BaseView(), SiteListener, AnkoLogger {

    lateinit var presenter: SiteListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_list_view)
        setSupportActionBar(toolbar)

        presenter = initPresenter(SiteListPresenter(this)) as SiteListPresenter

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        presenter.loadSites()
    }

    override fun showSites(sites: List<Site>) {
        recyclerView.adapter = SiteAdapter(sites, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_add -> presenter.openNewSiteActivity()
            R.id.item_logout -> presenter.logout()
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
