package de.othr.archeologicalfieldwork.views.site

import android.annotation.SuppressLint
import android.content.Intent
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import de.othr.archeologicalfieldwork.R
import de.othr.archeologicalfieldwork.helper.checkLocationPermissions
import de.othr.archeologicalfieldwork.helper.createDefaultLocationRequest
import de.othr.archeologicalfieldwork.helper.isPermissionGranted
import de.othr.archeologicalfieldwork.helper.showImagePicker
import de.othr.archeologicalfieldwork.main.MainApp
import de.othr.archeologicalfieldwork.model.Location
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.views.BasePresenter
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class SitePresenter(view: SiteView) : BasePresenter(view), AnkoLogger {

    private var edit = false
    private var locationAccess = false
    private val IMAGE_REQUEST = 1

    private var siteView: SiteView = view
    var defaultLocation = Location(49.013432, 12.101624, 15f)

    var locationManualyChanged = false
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
        view.requireActivity()
    )
    private val locationRequest = createDefaultLocationRequest()

    init {
        app = view.requireActivity().application as MainApp
        val args: SiteViewArgs by view.navArgs()

        locationAccess = checkLocationPermissions(view.requireActivity())

        if (args.siteEdit == null) {
            view.showSite(siteView.site, siteView.getIsVisited())
            locationManualyChanged = true
        } else {
            edit = true
            val visited = app.userStore.getCurrentUser()?.visitedSites

            if (visited == null) {
                view.showSite(args.siteEdit!!, false)
            } else {
                view.showSite(args.siteEdit!!, visited.contains(args.siteEdit!!.id))
            }
        }
    }

    override fun doRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (isPermissionGranted(requestCode, grantResults)) {
            info("location granted")
        } else {
            info("location not granted")
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

        this.openSiteList()
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }

    fun doDelete(site: Site) {
        app.siteStore.delete(site)
        this.openSiteList()
    }

    fun openSiteList() {
        Navigation.findNavController(this.view?.requireView()!!).navigate(R.id.action_siteView_to_siteListView)
    }

    fun openLocationMap() {
        Navigation.findNavController(this.view?.requireView()!!).navigate(
            SiteViewDirections.actionSiteViewToLocationMapView(
                siteView.site
            )
        )
    }

    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        info("Got back $requestCode")

        when (requestCode) {
            IMAGE_REQUEST -> {
                var images = ArrayList<String>()

                if (data.clipData != null) {
                    for (i in 0 until data.clipData!!.itemCount) {
                        var image = data.clipData!!.getItemAt(i).uri.toString()
                        images.add(image)
                    }
                } else if (data.data != null) {
                    images.add(data.data.toString())
                }

                siteView.updateImages(images)
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
        openLocationMap()
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

    fun setFav(site: Site) {
        if (app.userStore.isFavorite(site)) {
            app.userStore.removeFavoriteSite(site)
            siteView.setFavSelectionOff()

            info("Removed favorite: ${site.id}")
        } else {
            app.userStore.addFavoriteSite(site.id)
            siteView.setFavSelectionOn()

            info("Added favorite: ${site.id}")
        }
    }

    fun doRating(site: Site, rating: Float) {
        app.siteStore.addRating(site, app.userStore.getCurrentUser()!!, rating)
    }

    fun share(site: Site) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Site: ${site.name} at ${site.location.lat}/${site.location.lng}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        siteView.startActivity(shareIntent)
    }

    fun setupMenu() {
        if (app.userStore.isFavorite(siteView.site)) {
            siteView.setFavSelectionOn()
        } else {
            siteView.setFavSelectionOff()
        }
    }
}