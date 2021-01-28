package de.othr.archeologicalfieldwork.model.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.othr.archeologicalfieldwork.model.Site
import de.othr.archeologicalfieldwork.model.User
import de.othr.archeologicalfieldwork.model.UserStore
import de.othr.archeologicalfieldwork.model.UserUpdateState
import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import java.util.*
import kotlin.collections.ArrayList

class FirebaseUserStore : UserStore, AnkoLogger {

    private data class UserData(var visitedSites : MutableMap<String, Date>,
                                var favoriteSites : MutableList<String>)

    private val DB_USERS = "userdata"
    
    private val mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private lateinit var db: DatabaseReference
    private var userData: UserData? = null

    init {
        doAsync {
            db = FirebaseDatabase.getInstance().reference
        }
    }

    override fun login(email: String, password: String, callback: Progressable) {
        callback.start()
        mAuth!!.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                fetchUserData { callback.done() }
            } else {
                callback.failure()
            }
        }
    }

    private fun fetchUserData(dataReady: () -> Unit) {
        val user = mAuth!!.currentUser!!
        var tmpUserData = ArrayList<UserData>()
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(tmpUserData) { it.getValue(UserData::class.java) }

                if (tmpUserData.isEmpty()) {
                    userData = UserData(HashMap(), ArrayList())
                } else {
                    userData = tmpUserData.first()
                }

                dataReady()
            }
        }

        db = FirebaseDatabase.getInstance().reference
        db.child(DB_USERS).child(user.uid).addListenerForSingleValueEvent(valueEventListener)
    }

    override fun logout() {
        mAuth!!.signOut()
    }

    override fun signup(email: String, password: String, callback: Progressable) {
        callback.start()
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                db.child(DB_USERS).child(mAuth!!.currentUser!!.uid).setValue(UserData(HashMap(), ArrayList()))
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

        return User(firebaseuser.uid, firebaseuser.email!!, "", userData!!.visitedSites, userData!!.favoriteSites)
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

    override fun addVisitedSite(id: String) {
        userData!!.visitedSites[id] = Date()
        db.child(DB_USERS).child(mAuth!!.currentUser!!.uid).setValue(userData)
    }

    override fun addFavoriteSite(id: String) {
        userData!!.favoriteSites.add(id)
        db.child(DB_USERS).child(mAuth!!.currentUser!!.uid).setValue(userData)
    }

    override fun removeVisitedSite(id: String) {
        userData!!.visitedSites.remove(id)
        db.child(DB_USERS).child(mAuth!!.currentUser!!.uid).setValue(userData)
    }

    override fun isFavorite(site: Site): Boolean {
        return userData!!.favoriteSites.find { s -> s == site.id } != null
    }

    override fun removeFavoriteSite(site: Site) {
        userData!!.favoriteSites.remove(site.id)
        db.child(DB_USERS).child(mAuth!!.currentUser!!.uid).setValue(userData)
    }
}