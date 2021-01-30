package de.othr.archeologicalfieldwork.views.map

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BaseView
import kotlinx.android.synthetic.main.activity_site_map.*
import org.jetbrains.anko.AnkoLogger

class SiteMapView : BaseView(R.layout.activity_site_map), AnkoLogger, GoogleMap.OnMarkerClickListener {

    lateinit var presenter: SiteMapPresenter
    lateinit var map : GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = initPresenter(SiteMapPresenter(this)) as SiteMapPresenter

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync {
            map = it
            map.setOnMarkerClickListener(this)
            presenter.loadPlacemarks()
        }
    }

    override fun showSite(site: Site, visited: Boolean) {
        currentTitle.text = site.name
        currentDescription.text = site.description
        Glide.with(this).load(site.images.firstOrNull()).into(currentImage);
    }

    override fun showSites(sites: List<Site>) {
        presenter.doPopulateMap(map, sites)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}