package de.othr.archeologicalfieldwork.model

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.warn
import java.util.concurrent.atomic.AtomicLong

class UserMemStore : UserStore, AnkoLogger {

    var users = ArrayList<User>()
    private val userCounter = AtomicLong()

    var user: User? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        user = null
    }

    override fun login(email: String, password: String): Boolean {
        val user = this.users.find { u -> u.email.equals(email) }

        if (user != null) {
            if (user.password.equals(password)) {
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
        if (this.doesUserExist(email)) {
            val id = this.userCounter.getAndIncrement()
            val fakeUser = User(id, email, password)
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
            info("Deleted user ${user?.id} : ${user?.email}")

            return true
        } else {
            info("Tried to delete unknown user ${user?.email}")

            return false
        }
    }

    override fun doesUserExist(email: String): Boolean {
        return this.users.find { u -> u.email.equals(email) } == null
    }
}