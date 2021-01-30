package de.othr.archeologicalfieldwork.views.location

import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter

class LocationMapPresenter(locationView: LocationMapView) : BasePresenter(locationView) {

    private val site: Site

    init {
        val args: LocationMapViewArgs by locationView.navArgs()
        site = args.site
    }

    fun initMap(map: GoogleMap) {
        val loc = LatLng(site.location.lat, site.location.lng)
        val options = MarkerOptions()
            .title("Site")
            .snippet("GPS : " + loc.toString())
            .draggable(true)
            .position(loc)
        map.uiSettings.isZoomControlsEnabled = true
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, site.location.zoom))
    }

    fun doUpdateLocation(lat: Double, lng: Double, zoom: Float) {
        site.location.lat = lat
        site.location.lng = lng
        site.location.zoom = zoom
    }

    fun openSite() {
        Navigation.findNavController(this.view?.requireView()!!).navigate(LocationMapViewDirections.actionLocationMapViewToSiteView(site))
    }
    
    fun doUpdateMarker(marker: Marker) {
        val loc = LatLng(site.location.lat, site.location.lng)
        marker.setSnippet("GPS : " + loc.toString())
    }
}