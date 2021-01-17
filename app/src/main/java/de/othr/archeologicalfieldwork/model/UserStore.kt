package de.othr.archeologicalfieldwork.model

interface UserStore {
    fun login(email: String, password: String): Boolean
    fun logout()
    fun signup(email: String, password: String): Boolean
    fun delete(user: User): Boolean
    fun doesUserExist(email: String): Boolean
}