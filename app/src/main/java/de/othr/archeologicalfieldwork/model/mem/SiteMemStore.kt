package de.othr.archeologicalfieldwork.model.mem

import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.SiteStore
import de.othr.archeologicalfieldwork.model.User
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.*
import kotlin.collections.ArrayList

class SiteMemStore : SiteStore, AnkoLogger {

    var sites = ArrayList<Site>()

    override fun findAll(): List<Site> {
        return this.sites
    }

    override fun findById(id: String): Site? {
        return sites.find { it.id == id }
    }

    override fun create(site: Site): Site {
        site.id = UUID.randomUUID().toString()
        this.sites.add(site)

        info("Created new site: $site")
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
            persistedSite.rating = site.rating

            info("Update site: $site")
        } else {
            error("Tried to update site which does not exist")
        }
    }

    override fun delete(site: Site) {
        val persistedSite = this.findById(site.id)

        if (persistedSite != null) {
            this.sites.remove(persistedSite)
            info("Deleted site: $site")
        } else {
            error("Tried to delete site which does not exist: $site")
        }
    }

    override fun resolveIds(ids: List<String>?): List<Site> {
        val sites = ArrayList<Site>()
        ids?.forEach { findById(it)?.let { it1 -> sites.add(it1) }}

        return sites
    }

    override fun searchForName(text: String): List<Site> {
        return sites.filter { it.name.contains(text) }
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
        }
    }
}
