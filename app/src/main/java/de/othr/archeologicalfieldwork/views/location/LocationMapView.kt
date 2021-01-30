package de.othr.archeologicalfieldwork.views.location

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.views.BaseView

class LocationMapView : BaseView(R.layout.activity_location), GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    lateinit var map: GoogleMap
    lateinit var presenter: LocationMapPresenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        setHasOptionsMenu(true)

        presenter = initPresenter(LocationMapPresenter(this)) as LocationMapPresenter

        mapFragment!!.getMapAsync {
            map = it
            map.setOnMarkerDragListener(this)
            map.setOnMarkerClickListener(this)
            presenter.initMap(map)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_blank, menu)
    }

    override fun onMarkerDragStart(marker: Marker) {}

    override fun onMarkerDrag(marker: Marker) {}

    override fun onMarkerDragEnd(marker: Marker) {
        presenter.doUpdateLocation(
            marker.position.latitude,
            marker.position.longitude,
            map.cameraPosition.zoom
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doUpdateMarker(marker)

        return false
    }
}