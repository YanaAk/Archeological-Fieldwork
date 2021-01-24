package de.othr.archeologicalfieldwork.views.site

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.model.Location
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BaseView
import de.othr.archeologicalfieldwork.views.site.images.SiteImagesAdapter
import de.othr.archeologicalfieldwork.views.site.images.SiteImagesListener
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast


class SiteView : BaseView(), SiteImagesListener, AnkoLogger, OnMapReadyCallback {

    lateinit var presenter: SitePresenter
    var site = Site()
    lateinit var mMapView: MapView
    private lateinit var gMap: GoogleMap

    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)

        init(siteToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = initPresenter(SitePresenter(this)) as SitePresenter

        val layoutManager = LinearLayoutManager(this)
        site_images_recycler.layoutManager = layoutManager
        chooseImage.setOnClickListener { presenter.doSelectImage() }

        var mapViewBundle: Bundle? = null

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMapView = findViewById<View>(R.id.siteMapLocation) as MapView
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    override fun showSite(site: Site, visited: Boolean) {
        this.site = site
        siteName.setText(site.name)
        siteDescription.setText(site.description)
        siteNotes.setText(site.notes)
        visitedCheckBox.isChecked = visited
        this.updateImages(site.images)
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
        when (item.itemId) {
            R.id.item_save -> {
                if (siteName.text.toString().isEmpty()) {
                    toast(R.string.enter_site_name)
                } else {
                    site.name = siteName.text.toString()
                    site.description = siteDescription.text.toString()
                    site.notes = siteNotes.text.toString()
                    val visited = visitedCheckBox.isChecked
                    presenter.doAddOrSave(site, visited)
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

    fun setLocation(location: Location) {
        val latlng = LatLng(location.lat, location.lng)
        gMap.clear()
        gMap.addMarker(MarkerOptions().position(latlng))
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12.0f))
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        gMap = googleMap!!
        googleMap?.setOnMapClickListener { presenter.doSetLocation() }
        presenter.initMap()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)

        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }

        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
        presenter.doResartLocationUpdates()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}