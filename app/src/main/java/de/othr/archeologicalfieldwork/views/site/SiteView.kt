package de.othr.archeologicalfieldwork.views.site

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.helper.readImageFromPath
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BaseView
import de.othr.archeologicalfieldwork.views.site.images.SiteImagesAdapter
import de.othr.archeologicalfieldwork.views.site.images.SiteImagesListener
import de.othr.archeologicalfieldwork.views.sitelist.SiteAdapter
import kotlinx.android.synthetic.main.activity_site.*
import kotlinx.android.synthetic.main.activity_site.siteDescription
import kotlinx.android.synthetic.main.activity_site_list_view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class SiteView : BaseView(), SiteImagesListener, AnkoLogger {

    lateinit var presenter: SitePresenter
    var site = Site()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        init(siteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = initPresenter (SitePresenter(this)) as SitePresenter

        val layoutManager = LinearLayoutManager(this)
        site_images_recycler.layoutManager = layoutManager
        chooseImage.setOnClickListener { presenter.doSelectImage() }
    }

    override fun showSite(site: Site) {
        siteName.setText(site.name)
        siteDescription.setText(site.description)
        siteNotes.setText(site.notes)
        this.updateImages(site.images)
        this.site = site
    }

    fun updateImages(images: List<String>) {
        this.site.images = images

        site_images_recycler.adapter = SiteImagesAdapter(this.site.images, this)
        site_images_recycler.adapter?.notifyDataSetChanged()

        if (this.site.images != null) {
            chooseImage.setText(R.string.change_site_image)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_site, menu)

        if (this.site.name.isBlank()) {
            // new site
            val deleteButton = menu.findItem(R.id.item_delete)
            deleteButton.isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_save -> {
                if (siteName.text.toString().isEmpty()) {
                    toast(R.string.enter_site_name)
                } else {
                    site.name = siteName.text.toString()
                    site.description = siteDescription.text.toString()
                    site.notes = siteNotes.text.toString()
                    presenter.doAddOrSave(site)
                }
            }
            R.id.item_delete -> {
                presenter.doDelete(site)
            }
            R.id.item_cancel -> {
                presenter.doCancel()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            presenter.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        presenter.doCancel()
    }

    override fun onImageClick(image: String) {
        info("Image $image clicked")
    }
}