package de.othr.archeologicalfieldwork.model.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.othr.archeologicalfieldwork.helper.readImageFromPath
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.SiteStore
import de.othr.archeologicalfieldwork.model.User
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import java.io.ByteArrayOutputStream
import java.io.File

class FirebaseSiteStore(val context: Context) : SiteStore, AnkoLogger {

    private val DB_SITES = "sites"

    private var sites = ArrayList<Site>()
    private lateinit var db: DatabaseReference
    lateinit var st: StorageReference

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
            updateImages(persistedSite)

            info("Update site: $site")
        } else {
            error("Tried to update site which does not exist")
        }
    }

    private fun updateImages(site: Site) {
        val images = ArrayList(site.images)

        for (image in images) {
            if (image != "" && image.startsWith("content://")) {
                val fileName = File(image)
                val imageName = fileName.name

                var imageRef = st.child(site.id + '/' + imageName)
                val baos = ByteArrayOutputStream()
                val bitmap = readImageFromPath(context, image)

                bitmap?.let {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        println(it.message)
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            site.images.remove(image)
                            site.images.add(it.toString())
                            db.child(DB_SITES).child(site.id).child("images").setValue(site.images)
                        }
                    }
                }
            }
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
                dataSnapshot.children.mapNotNullTo(sites) { it.getValue(Site::class.java) }
                sitesReady()
            }
        }

        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference
        sites.clear()
        db.child(DB_SITES).addListenerForSingleValueEvent(valueEventListener)
    }
}