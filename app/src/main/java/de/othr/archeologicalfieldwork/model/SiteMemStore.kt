package de.othr.archeologicalfieldwork.model

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.concurrent.atomic.AtomicLong

class SiteMemStore : SiteStore, AnkoLogger {

    var sites = ArrayList<Site>()
    private val siteCounter = AtomicLong()

    override fun findAll(): List<Site> {
        return this.sites
    }

    override fun findById(id: Long): Site? {
        return sites.find { it.id == id }
    }

    override fun create(site: Site): Site {
        site.id = this.siteCounter.getAndIncrement()
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

    override fun resolveIds(ids: List<Long>?): List<Site> {
        val sites = ArrayList<Site>()
        ids?.forEach { findById(it)?.let { it1 -> sites.add(it1) }}

        return sites
    }
}
