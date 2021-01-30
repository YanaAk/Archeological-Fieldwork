package de.othr.archeologicalfieldwork.views.site

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
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


class SiteView : BaseView(R.layout.activity_site), SiteImagesListener, AnkoLogger, OnMapReadyCallback {

    private lateinit var menu: Menu
    lateinit var presenter: SitePresenter
    var site = Site()
    lateinit var mMapView: MapView
    private lateinit var gMap: GoogleMap
    var ratingbar: RatingBar? = null

    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = initPresenter(SitePresenter(this)) as SitePresenter

        setHasOptionsMenu(true)

        val layoutManager = LinearLayoutManager(activity)
        site_images_recycler.layoutManager = layoutManager
        chooseImage.setOnClickListener { presenter.doSelectImage() }

        var mapViewBundle: Bundle? = null

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMapView = view.findViewById<View>(R.id.siteMapLocation) as MapView
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)

        setupRatingBar()
    }

    private fun setupRatingBar() {
        ratingbar = view?.findViewById<View>(R.id.ratingBar) as RatingBar
        ratingbar!!.rating = this.site.rating.rating

        ratingButton.setOnClickListener {
            val rating: Float = ratingbar!!.rating
            presenter.doRating(site, rating)
            info("Rated $site with $rating")
        }
    }

    override fun showSite(site: Site, visited: Boolean) {
        this.site = site
        siteName.setText(site.name)
        siteDescription.setText(site.description)
        siteNotes.setText(site.notes)
        visitedCheckBox.isChecked = visited
        this.updateImages(site.images)
    }

    fun updateImages(images: MutableList<String>) {
        this.site.images = images

        site_images_recycler.adapter = SiteImagesAdapter(this.site.images, this)
        site_images_recycler.adapter?.notifyDataSetChanged()

        if (this.site.images != null) {
            chooseImage.setText(R.string.change_site_image)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_site, menu)
        this.menu = menu

        if (this.site.name.isBlank()) {
            // new site
            val deleteButton = menu.findItem(R.id.item_delete)
            deleteButton.isVisible = false

            hideFavSelection()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_save -> {
                if (siteName.text.toString().isEmpty()) {
                    Toast.makeText(activity, R.string.enter_site_name, Toast.LENGTH_SHORT)
                } else {
                    site.name = siteName.text.toString()
                    site.description = siteDescription.text.toString()
                    site.notes = siteNotes.text.toString()
                    val visited = visitedCheckBox.isChecked
                    presenter.doAddOrSave(site, visited)
                }
            }
            R.id.item_delete -> presenter.doDelete(site)
            R.id.item_mark_fav -> presenter.setFav(site)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            presenter.doActivityResult(requestCode, resultCode, data)
        }
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
        googleMap.setOnMapClickListener { presenter.doSetLocation() }
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

    fun hideFavSelection() {
        menu.findItem(R.id.item_mark_fav).isVisible = false
    }
}