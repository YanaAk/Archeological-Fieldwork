package de.othr.archeologicalfieldwork.model.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.User
import de.othr.archeologicalfieldwork.model.UserStore
import de.othr.archeologicalfieldwork.model.UserUpdateState
import de.othr.archeologicalfieldwork.model.json.UserJsonStore
import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error

class FirebaseUserStore(context: Context) : UserStore, AnkoLogger {
    
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private val userData: UserJsonStore = UserJsonStore(context)

    override fun login(email: String, password: String, callback: Progressable) {
        callback.start()
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                getCurrentUser()?.let {
                    if (!userData.users.contains(it)) {
                        userData.users.add(it)
                    }
                }

                callback.done()
            } else {
                callback.failure()
            }
        }
    }

    override fun logout() {
        mAuth!!.signOut()
    }

    override fun signup(email: String, password: String, callback: Progressable) {
        callback.start()
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                getCurrentUser()?.let { userData.users.add(it) }
                callback.done()
            } else {
                error(task.exception)
                callback.failure()
            }
        }
    }

    override fun doesUserExist(email: String, callback: ProgressableForResult<Boolean, Void>) {
        callback.start()
        mAuth!!.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            val isNewUser = task.result.signInMethods!!.isEmpty()

            callback.done(!isNewUser)
        }
    }

    override fun getCurrentUser(): User? {
        val firebaseuser = mAuth!!.currentUser!!
        userData.user = userData.users.find { u -> u.id == firebaseuser.uid }

        return User(firebaseuser.uid, firebaseuser.email!!, "", userData.user!!.visitedSites, userData.user!!.favoriteSites)
    }

    override fun updateUser(
        id: String?,
        accountEmail: String,
        accountPassword: String,
        callback: ProgressableForResult<UserUpdateState, UserUpdateState>
    ) {
        val currentUser = mAuth!!.currentUser!!

        if (currentUser.email != accountEmail) {
            currentUser.updateEmail(accountPassword).addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser.updatePassword(accountPassword).addOnCompleteListener {
                        if (it.isSuccessful) {
                            callback.done(UserUpdateState.SUCCESS)
                        } else {
                            callback.done(UserUpdateState.FAILURE_USERNAME_USED)
                        }
                    }
                } else {
                    callback.done(UserUpdateState.FAILURE_USERNAME_USED)
                }
            }
        } else {
            currentUser.updatePassword(accountPassword).addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.done(UserUpdateState.SUCCESS)
                } else {
                    callback.done(UserUpdateState.FAILURE_PASSWORD_ERROR)
                }
            }
        }
    }

    override fun addVisitedSite(id: Long) {
        userData.user = userData.users.find { u -> u.id == getCurrentUser()?.id }
        userData.addVisitedSite(id)
    }

    override fun addFavoriteSite(id: Long) {
        userData.user = userData.users.find { u -> u.id == getCurrentUser()?.id }
        userData.addFavoriteSite(id)
    }

    override fun removeVisitedSite(id: Long) {
        userData.user = userData.users.find { u -> u.id == getCurrentUser()?.id }
        userData.removeVisitedSite(id)
    }

    override fun hasFavorite(site: Site): Boolean {
        userData.user = userData.users.find { u -> u.id == getCurrentUser()?.id }
        return userData.hasFavorite(site)
    }

    override fun removeFavoriteSite(site: Site) {
        userData.user = userData.users.find { u -> u.id == getCurrentUser()?.id }
        userData.removeFavoriteSite(site)
    }
}