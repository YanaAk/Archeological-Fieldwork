package de.othr.archeologicalfieldwork.views.site

import android.content.Intent
import de.othr.archeologicalfieldwork.helper.showImagePicker
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.model.Location
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.VIEW

class SitePresenter(view: SiteView) : BasePresenter(view) {

    private var edit = false
    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2

    private var siteView: SiteView = view
    var defaultLocation = Location(49.013432, 12.101624, 15f)

    init {
        app = view.application as MainApp

        if (view.intent.hasExtra("site_edit")) {
            edit = true
            val site = view.intent.extras?.getParcelable<Site>("site_edit")!!
            val visited = app.userStore.getCurrentUser()?.visitedSites?.contains(site.id)

            if (visited == null) {
                view.showSite(site, false)
            } else {
                view.showSite(site, visited)
            }
        }
    }

    fun doAddOrSave(site: Site, visited: Boolean) {
        if (!edit) {
            val createdSite = app.siteStore.create(site)
            site.id = createdSite.id
        } else {
            app.siteStore.update(site)
        }

        if (visited) {
            app.userStore.addVisitedSite(site.id)
        } else {
            app.userStore.removeVisitedSite(site.id)
        }

        view?.finish()
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }

    fun doDelete(site: Site) {
        app.siteStore.delete(site)
        view?.finish()
    }

    fun doCancel() {
        view?.finish()
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                var images = ArrayList<String>()

                if(data.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        var image = data.clipData!!.getItemAt(i).uri.toString()
                        images.add(image)
                    }
                } else if(data.data != null) {
                    images.add(data.data.toString())
                }

                siteView.updateImages(images)
            }
            LOCATION_REQUEST -> {
                val location = data.extras?.getParcelable<Location>("location")!!
                siteView.site.location = location
                siteView.setLocation(location)
            }
        }
    }

    fun doSetLocation() {
        if (!edit) {
            view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", defaultLocation)
        } else {
            view?.navigateTo(
                VIEW.LOCATION,
                LOCATION_REQUEST,
                "location",
                siteView.site.location
            )
        }
    }
}