package de.othr.archeologicalfieldwork.model

import de.othr.archeologicalfieldwork.views.Progressable
import de.othr.archeologicalfieldwork.views.ProgressableForResult

interface UserStore {
    fun login(email: String, password: String, callback: Progressable)
    fun logout()
    fun signup(email: String, password: String, callback: Progressable)
    fun doesUserExist(email: String, callback: ProgressableForResult<Boolean, Void>)
    fun getCurrentUser(): User?
    fun updateUser(id: String?, accountEmail: String, accountPassword: String,
                   callback: ProgressableForResult<UserUpdateState, UserUpdateState>)
    fun addVisitedSite(id: String)
    fun addFavoriteSite(id: String)
    fun removeVisitedSite(id: String)
    fun hasFavorite(site: Site): Boolean
    fun removeFavoriteSite(site: Site)
}