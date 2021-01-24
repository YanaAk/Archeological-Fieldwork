package de.othr.archeologicalfieldwork.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SiteMapPresenter(siteView: SiteMapView) : BasePresenter(siteView), AnkoLogger {

    fun doPopulateMap(map: GoogleMap, sites: List<Site>) {
        map.uiSettings.setZoomControlsEnabled(true)

        sites.forEach {
            val loc = LatLng(it.location.lat, it.location.lng)
            val options = MarkerOptions().title(it.name).position(loc)
            map.addMarker(options).tag = it
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))
        }
    }

    fun doMarkerSelected(marker: Marker) {
        val site = marker.tag as Site

        doAsync {
            uiThread {
                if (site != null) {
                    val visited = app.userStore.getCurrentUser()?.visitedSites?.contains(site.id)
                    view?.showSite(site, visited!!)
                }
            }
        }
    }

    fun loadPlacemarks() {
        doAsync {
            val sites = app.siteStore.findAll()

            uiThread {
                view?.showSites(sites)
            }
        }
    }
}
