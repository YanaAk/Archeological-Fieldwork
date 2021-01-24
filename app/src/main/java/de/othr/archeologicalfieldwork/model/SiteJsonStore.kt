package de.othr.archeologicalfieldwork.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.othr.archeologicalfieldwork.helper.exists
import de.othr.archeologicalfieldwork.helper.read
import de.othr.archeologicalfieldwork.helper.write
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

class SiteJsonStore : SiteStore, AnkoLogger {

    private val jsonFile = "sites.json"
    private val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting().create()
    private val listType = object : TypeToken<ArrayList<Site>>() {}.type

    private var context: Context
    private var sites = ArrayList<Site>()
    private val siteCounter = AtomicLong()

    constructor (context: Context) {
        this.context = context

        if (exists(context, jsonFile)) {
            deserialize()
        }
    }

    override fun findAll(): List<Site> {
        return this.sites
    }

    override fun findById(id: Long): Site? {
        return sites.find { it.id == id }
    }

    override fun create(site: Site): Site {
        site.id = this.siteCounter.getAndIncrement()
        this.sites.add(site)
        serialize()

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

            serialize()

            info("Update site: $site")
        } else {
            error("Tried to update site which does not exist")
        }
    }

    override fun delete(site: Site) {
        val persistedSite = this.findById(site.id)

        if (persistedSite != null) {
            this.sites.remove(persistedSite)
            serialize()
            info("Deleted site: $site")
        } else {
            error("Tried to delete site which does not exist: $site")
        }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(this.sites, listType)
        write(context, jsonFile, jsonString)

        info("Saved sites to file")
    }

    private fun deserialize() {
        val jsonString = read(context, jsonFile)
        this.sites = Gson().fromJson(jsonString, listType)

        var maxId = 0L

        for (u in this.sites) {
            maxId = max(maxId, u.id)
        }

        this.siteCounter.set(maxId)

        info("Read sites from file")
    }
}