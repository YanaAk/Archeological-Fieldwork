package de.othr.archeologicalfieldwork.views

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.site.SiteView
import de.othr.archeologicalfieldwork.views.sitelist.SiteListView
import org.jetbrains.anko.AnkoLogger

enum class VIEW {
    LIST, SITE
}

open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null) {
        var intent = Intent(this, SiteListView::class.java)

        when (view) {
            VIEW.LIST -> intent = Intent(this, SiteListView::class.java)
            VIEW.SITE -> intent = Intent(this, SiteView::class.java)
        }

        if (key != "") {
            intent.putExtra(key, value)
        }

        startActivityForResult(intent, code)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter {
        basePresenter = presenter
        return presenter
    }

    fun init(toolbar: Toolbar) {
        toolbar.title = title
        setSupportActionBar(toolbar)
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        basePresenter?.doRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun showSite(site: Site) {}
    open fun showSites(sites: List<Site>) {}
    open fun showProgress() {}
    open fun hideProgress() {}
}