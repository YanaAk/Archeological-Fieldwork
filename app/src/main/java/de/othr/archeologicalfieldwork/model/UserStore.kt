package de.othr.archeologicalfieldwork.model

interface UserStore {
    fun login(email: String, password: String): Boolean
    fun logout()
    fun signup(email: String, password: String): Boolean
    fun delete(user: User): Boolean
    fun doesUserExist(email: String): Boolean
    fun getCurrentUser(): User?
    fun updateUser(id: Long?, accountEmail: String, accountPassword: String): UserUpdateState
    fun addVisitedSite(id: Long)
    fun addFavoriteSite(id: Long)
    fun removeVisitedSite(id: Long)
    fun hasFavorite(site: Site): Boolean
    fun removeFavoriteSite(site: Site)
}