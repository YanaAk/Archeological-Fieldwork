package de.othr.archeologicalfieldwork.model

import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult

interface UserStore {
    fun login(email: String, password: String, callback: Progressable)
    fun logout()
    fun signup(email: String, password: String, callback: Progressable)
    fun doesUserExist(email: String, callback: ProgressableForResult<Boolean, Void>)
    fun getCurrentUser(): User?
    fun updateUser(id: String?, accountEmail: String, accountPassword: String): UserUpdateState
    fun addVisitedSite(id: Long)
    fun addFavoriteSite(id: Long)
    fun removeVisitedSite(id: Long)
    fun hasFavorite(site: Site): Boolean
    fun removeFavoriteSite(site: Site)
}