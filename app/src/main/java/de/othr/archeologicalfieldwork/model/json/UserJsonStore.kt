package de.othr.archeologicalfieldwork.model.json

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import de.othr.archeologicalfieldwork.helper.exists
import de.othr.archeologicalfieldwork.helper.read
import de.othr.archeologicalfieldwork.helper.write
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.User
import de.othr.archeologicalfieldwork.model.UserStore
import de.othr.archeologicalfieldwork.model.UserUpdateState
import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserJsonStore: UserStore, AnkoLogger {

    private val jsonFile = "users.json"
    private val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting().create()
    private val listType = object : TypeToken<ArrayList<User>>() {}.type

    private var context: Context
    private var users = mutableListOf<User>()

    var user: User? = null
        private set

    constructor (context: Context) {
        this.context = context

        if (exists(context, jsonFile)) {
            deserialize()
        }
    }

    init {
        user = null
    }

    override fun login(email: String, password: String, callback: Progressable) {
        callback.start()
        val user = this.users.find { u -> u.email == email.trim() }

        if (user != null) {
            if (user.password == password) {
                this.user = user
                info("Login successful: $email")
                callback.done()
            } else {
                warn("Login failed: Wrong password for $email")
                callback.failure()
            }
        } else {
            warn("Login failed: User does not exist $email")
            callback.failure()
        }
    }

    override fun logout() {
        user = null
    }

    override fun signup(email: String, password: String, callback: Progressable) {
        this.doesUserExist(email, object : ProgressableForResult<Boolean, Void> {
            override fun start() {}

            override fun done(r: Boolean) {
                if (!r) {
                    val newUser = User(UUID.randomUUID().toString(), email, password, HashMap(), ArrayList())
                    users.add(newUser)
                    user = newUser
                    serialize()
                    info("Signup successful: ${newUser.id} : $email")
                    callback.done()
                } else {
                    error("Signup failed: $email. E-Mail is already in use")
                    callback.failure()
                }
            }

            override fun failure(r: Void) {}
        })
    }

    fun delete(user: User): Boolean {
        val persistedUser = this.users.find { u -> u.id == user.id}

        if (persistedUser != null) {
            this.users.remove(persistedUser)
            serialize()
            info("Deleted user ${persistedUser .id} : ${persistedUser.email}")

            return true
        } else {
            info("Tried to delete unknown user ${persistedUser?.email}")

            return false
        }
    }

    override fun doesUserExist(email: String, callback: ProgressableForResult<Boolean, Void>) {
        callback.start()
        callback.done(this.users.find { u -> u.email == email.trim() } != null)
    }

    override fun getCurrentUser(): User? {
        return this.user
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(this.users, listType)
        write(context, jsonFile, jsonString)

        info("Saved users to file")
    }

    private fun deserialize() {
        val jsonString = read(context, jsonFile)
        this.users = Gson().fromJson(jsonString, listType)

        info("Read users from file")
    }

    override fun updateUser(id: String?, accountEmail: String, accountPassword: String): UserUpdateState {
        val user = this.users.find { u -> u.id == id } ?: return UserUpdateState.FAILURE_USER_NOT_FOUND

        if (user.email == accountEmail) {
            // same email -> only change password
            user.password = accountPassword
            serialize()

            return UserUpdateState.SUCCESS
        } else if (this.users.find {u -> u.email == accountEmail} != null){
            // change both email and password
            user.email = accountEmail
            user.password = accountPassword
            serialize()

            return UserUpdateState.SUCCESS
        } else {
            return UserUpdateState.FAILURE_USERNAME_USED
        }
    }

    override fun addVisitedSite(id: Long) {
        if (this.user?.visitedSites == null) {
            this.user?.visitedSites = HashMap()
        }

        this.user?.visitedSites?.put(id, Date())
        serialize()
    }

    override fun addFavoriteSite(id: Long) {
        if (this.user?.favoriteSites == null) {
            this.user?.favoriteSites = ArrayList()
        }

        this.user?.favoriteSites?.add(id)
        serialize()
    }

    override fun removeVisitedSite(id: Long) {
        this.user?.visitedSites?.remove(id)
        serialize()
    }

    override fun hasFavorite(site: Site): Boolean {
        val res = this.user?.favoriteSites?.find { sid -> sid == site.id }

        return res != null
    }

    override fun removeFavoriteSite(site: Site) {
        val res = this.user?.favoriteSites?.find { sid -> sid == site.id }

        if (res != null) {
            this.user?.favoriteSites?.remove(res)
            serialize()
        }
    }
}