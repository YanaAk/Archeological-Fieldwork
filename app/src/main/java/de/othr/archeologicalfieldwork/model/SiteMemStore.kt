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

    override fun create(site: Site) {
        site.id = this.siteCounter.getAndIncrement()
        this.sites.add(site)

        info("Created new site: $site")
    }

    override fun update(site: Site) {
        val persistedSite = this.findById(site.id)

        if (persistedSite != null) {
            persistedSite.description = site.description
            persistedSite.name = site.name
            persistedSite.images = site.images
            persistedSite.notes = site.notes

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
}
