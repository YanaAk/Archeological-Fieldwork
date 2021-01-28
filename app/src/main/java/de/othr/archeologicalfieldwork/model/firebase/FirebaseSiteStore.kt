package de.othr.archeologicalfieldwork.model.firebase

import com.google.firebase.database.*
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.SiteStore
import de.othr.archeologicalfieldwork.model.User
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info

class FirebaseSiteStore : SiteStore, AnkoLogger {

    private val DB_SITES = "sites"

    private var sites = ArrayList<Site>()
    lateinit var db: DatabaseReference

    init {
        doAsync {
            fetchSites { info("Loaded sites from db") }
        }
    }

    override fun findAll(): List<Site> {
        return sites
    }

    override fun findById(id: String): Site? {
        return sites.find { it.id == id }
    }

    override fun create(site: Site): Site {
        val key = db.child(DB_SITES).push().key
        key?.let {
            site.id = key
            sites.add(site)
            db.child(DB_SITES).child(key).setValue(site)
            info("Created new site: $site")
        }

        return site
    }

    override fun update(site: Site) {
        val persistedSite = this.findById(site.id)

        if (persistedSite != null) {
            persistedSite.description = site.description
            persistedSite.name = site.name
            persistedSite.images = site.images
            persistedSite.notes = site.notes
            persistedSite.location = site.location

            db.child(DB_SITES).child(persistedSite.id).setValue(persistedSite)

            info("Update site: $site")
        } else {
            error("Tried to update site which does not exist")
        }
    }

    override fun delete(site: Site) {
        db.child(DB_SITES).child(site.id).removeValue()
        sites.remove(site)
    }

    override fun resolveIds(ids: List<String>?): List<Site> {
        val sites = ArrayList<Site>()
        ids?.forEach { findById(it)?.let { it1 -> sites.add(it1) }}

        return sites
    }

    override fun addRating(site: Site, user: User, rating: Float) {
        val persistedSite = this.findById(site.id)

        if (persistedSite != null) {
            persistedSite.rating.userRating[user.id] = rating
            var overallRating = 0.0f

            for (r in persistedSite.rating.userRating) {
                overallRating += r.value
            }

            persistedSite.rating.rating = overallRating / persistedSite.rating.userRating.size
            db.child(DB_SITES).child(persistedSite.id).setValue(persistedSite)
            info("Added rating $rating for site: $site")
        }
    }

    private fun fetchSites(sitesReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(sites) { it.getValue(Site::class.java) }
                sitesReady()
            }
        }

        db = FirebaseDatabase.getInstance().reference
        sites.clear()
        db.child(DB_SITES).addListenerForSingleValueEvent(valueEventListener)
    }
}