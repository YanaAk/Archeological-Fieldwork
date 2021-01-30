package de.othr.archeologicalfieldwork.model.json

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.othr.archeologicalfieldwork.helper.exists
import de.othr.archeologicalfieldwork.helper.read
import de.othr.archeologicalfieldwork.helper.write
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.SiteStore
import de.othr.archeologicalfieldwork.model.User
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.*
import kotlin.collections.ArrayList

class SiteJsonStore(private var context: Context) : SiteStore, AnkoLogger {

    private val jsonFile = "sites.json"
    private val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting().create()
    private val listType = object : TypeToken<ArrayList<Site>>() {}.type

    private var sites = ArrayList<Site>()

    init {
        if (exists(context, jsonFile)) {
            deserialize()
        }
    }

    override fun findAll(): List<Site> {
        return this.sites
    }

    override fun findById(id: String): Site? {
        return sites.find { it.id == id }
    }

    override fun create(site: Site): Site {
        site.id = UUID.randomUUID().toString()
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
            persistedSite.rating = site.rating

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

            serialize()
        }
    }

    override fun searchForName(text: String): List<Site> {
        return sites.filter { it.name.contains(text) }
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(this.sites, listType)
        write(context, jsonFile, jsonString)

        info("Saved sites to file")
    }

    private fun deserialize() {
        val jsonString = read(context, jsonFile)
        this.sites = Gson().fromJson(jsonString, listType)

        info("Read sites from file")
    }
}