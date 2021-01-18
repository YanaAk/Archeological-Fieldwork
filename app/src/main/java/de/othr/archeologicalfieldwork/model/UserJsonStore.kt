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
import org.jetbrains.anko.warn
import java.util.concurrent.atomic.AtomicLong

internal val JSON_FILE = "users.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<User>>() {}.type

class UserJsonStore: UserStore, AnkoLogger {

    private var context: Context
    var users = mutableListOf<User>()
    private val userCounter = AtomicLong()

    var user: User? = null
        private set


    constructor (context: Context) {
        this.context = context

        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

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
            this.users.add(User(id, email, password))
            serialize()
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
            serialize()
            info("Deleted user ${user?.id} : ${user.email}")

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

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(this.users, listType)
        write(context, JSON_FILE, jsonString)

        info("Saved users to file")
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        this.users = Gson().fromJson(jsonString, listType)

        info("Read users from file")
    }
}