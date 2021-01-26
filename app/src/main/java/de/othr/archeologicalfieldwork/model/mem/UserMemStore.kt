package de.othr.archeologicalfieldwork.model.mem

import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.User
import de.othr.archeologicalfieldwork.model.UserStore
import de.othr.archeologicalfieldwork.model.UserUpdateState
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserMemStore : UserStore, AnkoLogger {

    var users = ArrayList<User>()
    private val userCounter = AtomicLong()

    var user: User? = null
        private set

    init {
        user = null
    }

    override fun login(email: String, password: String): Boolean {
        val user = this.users.find { u -> u.email == email.trim() }

        if (user != null) {
            if (user.password == password) {
                this.user = user
                info("Login successful: $email")

                return true
            } else {
                warn("Login failed: Wrong password for $email")

                return false
            }
        } else {
            warn("Login failed: User does not exist $email")

            return false
        }
    }

    override fun logout() {
        user = null
    }

    override fun signup(email: String, password: String): Boolean {
        if (!this.doesUserExist(email)) {
            val id = this.userCounter.getAndIncrement()
            this.users.add(User(id, email, password, HashMap(), ArrayList()))
            info("Signup successful: $id : $email")

            return true
        } else {
            error("Signup failed: $email. E-Mail is already in use")

            return false
        }
    }

    override fun delete(user: User): Boolean {
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

    override fun doesUserExist(email: String): Boolean {
        return this.users.find { u -> u.email == email.trim() } != null
    }

    override fun getCurrentUser(): User? {
        return this.user
    }

    override fun updateUser(id: Long?, accountEmail: String, accountPassword: String): UserUpdateState {
        val user = this.users.find { u -> u.id == id } ?: return UserUpdateState.FAILURE_USER_NOT_FOUND

        if (user.email == accountEmail) {
            // same email -> only change password
            user.password = accountPassword

            return UserUpdateState.SUCCESS
        } else if (this.users.find {u -> u.email == accountEmail} != null){
            // change both email and password
            user.email = accountEmail
            user.password = accountPassword

            return UserUpdateState.SUCCESS
        } else {
            return UserUpdateState.FAILURE_USERNAME_USED
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