package de.othr.archeologicalfieldwork.views.location

import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView

class LocationMapView : BaseView(), GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    lateinit var map: GoogleMap
    lateinit var presenter: LocationMapPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        presenter = initPresenter(LocationMapPresenter(this)) as LocationMapPresenter

        mapFragment.getMapAsync {
            map = it
            map.setOnMarkerDragListener(this)
            map.setOnMarkerClickListener(this)
            presenter.initMap(map)
        }
    }

    override fun onMarkerDragStart(marker: Marker) {}

    override fun onMarkerDrag(marker: Marker) {}

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.doUpdateLocation(marker.position.latitude, marker.position.longitude, map.cameraPosition.zoom)
    }

    override fun onBackPressed() {
        presenter.doOnBackPressed()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doUpdateMarker(marker)

        return false
    }
}