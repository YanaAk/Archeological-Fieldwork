package de.othr.archeologicalfieldwork.views.site

import android.content.Intent
import de.othr.archeologicalfieldwork.helper.showImagePicker
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.BaseView

class SitePresenter (view: SiteView) : BasePresenter(view) {

    private val IMAGE_REQUEST = 1

    private var siteView: SiteView = view

    init {
        app = view.application as MainApp
    }

    fun doAddOrSave(site: Site) {
        app.sites.add(site)

        view?.finish()
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }

    fun doCancel() {
        view?.finish()
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                siteView?.updateImage(data.data.toString())
            }
        }
    }
}