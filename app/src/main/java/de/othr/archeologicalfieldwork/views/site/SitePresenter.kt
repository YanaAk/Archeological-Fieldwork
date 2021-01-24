package de.othr.archeologicalfieldwork.views.site

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import de.othr.archeologicalfieldwork.helper.checkLocationPermissions
import de.othr.archeologicalfieldwork.helper.createDefaultLocationRequest
import de.othr.archeologicalfieldwork.helper.isPermissionGranted
import de.othr.archeologicalfieldwork.helper.showImagePicker
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.model.Location
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import de.othr.archeologicalfieldwork.views.VIEW
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class SitePresenter(view: SiteView) : BasePresenter(view), AnkoLogger {

    private var edit = false
    private var locationAccess = false
    private val IMAGE_REQUEST = 1
    private val LOCATION_REQUEST = 2

    private var siteView: SiteView = view
    var defaultLocation = Location(49.013432, 12.101624, 15f)

    var locationManualyChanged = false
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    val locationRequest = createDefaultLocationRequest()

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

        locationAccess = checkLocationPermissions(view)
    }

    override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isPermissionGranted(requestCode, grantResults)) {
            // todo get the current location
            info("called and granted")
        } else {
            info("called and not granted")
            // permissions denied, so use the default location
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
                locationManualyChanged = true
                info("Site location result: $location")
            }
        }
    }

    fun initMap() {
        if (locationAccess) {
            if (((siteView.site.location.lat > 0.1f || siteView.site.location.lat < -0.1f)
                && (siteView.site.location.lng > 0.1f || siteView.site.location.lng < -0.1f)
                && siteView.site.location.zoom > 0.0f)) {
                siteView.setLocation(siteView.site.location)
            } else {
                doSetCurrentLocation()
            }
        } else {
            siteView.setLocation(defaultLocation)
        }
    }

    fun doSetLocation() {
        if (!edit) {
            view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", siteView.site.location)
        } else {
            view?.navigateTo(
                VIEW.LOCATION,
                LOCATION_REQUEST,
                "location",
                siteView.site.location
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            siteView.site.location = Location(it.latitude, it.longitude, 12.0f)
            siteView.setLocation(siteView.site.location)
            info("Received current location ${siteView.site.location}")
        }
    }

    @SuppressLint("MissingPermission")
    fun doResartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()

                    if (!locationManualyChanged) {
                        siteView.setLocation(Location(l.latitude, l.longitude))
                    }
                }
            }
        }

        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }
}