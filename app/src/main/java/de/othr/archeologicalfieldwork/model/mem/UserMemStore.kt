package de.othr.archeologicalfieldwork.model.mem

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

class UserMemStore : UserStore, AnkoLogger {

    var users = ArrayList<User>()

    var user: User? = null
        private set

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
        val user = this.users.find { u -> u.id == user.id}

        if (user != null) {
            this.users.remove(user)
            info("Deleted user ${user.id} : ${user.email}")

            return true
        } else {
            info("Tried to delete unknown user ${user?.email}")

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

    override fun updateUser(id: String?, accountEmail: String, accountPassword: String, callback: ProgressableForResult<UserUpdateState, UserUpdateState>) {
        callback.start()
        val user = this.users.find { u -> u.id == id }

        if (user == null) {
            callback.failure(UserUpdateState.FAILURE_USER_NOT_FOUND)
        } else {
            if (user.email == accountEmail) {
                // same email -> only change password
                user.password = accountPassword

                callback.done(UserUpdateState.SUCCESS)
            } else if (this.users.find {u -> u.email == accountEmail} != null){
                // change both email and password
                user.email = accountEmail
                user.password = accountPassword

                callback.done(UserUpdateState.SUCCESS)
            } else {
                callback.failure(UserUpdateState.FAILURE_USERNAME_USED)
            }
        }
    }

    override fun addVisitedSite(id: Long) {
        this.user?.visitedSites?.put(id, Date())
    }

    override fun addFavoriteSite(id: Long) {
        this.user?.favoriteSites?.add(id)
    }

    override fun removeVisitedSite(id: Long) {
        this.user?.visitedSites?.remove(id)
    }

    override fun hasFavorite(site: Site): Boolean {
        val res = this.user?.favoriteSites?.find { sid -> sid == site.id }

        return res != null
    }

    override fun removeFavoriteSite(site: Site) {
        val res = this.user?.favoriteSites?.find { sid -> sid == site.id }

        if (res != null) {
            this.user?.favoriteSites?.remove(res)
        }
    }
}