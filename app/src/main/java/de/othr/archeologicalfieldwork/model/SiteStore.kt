package de.othr.archeologicalfieldwork.model

interface SiteStore {
    fun findAll(): List<Site>
    fun findById(id: Long): Site?
    fun create(site: Site): Site
    fun update(site: Site)
    fun delete(site: Site)
    fun resolveIds(ids: List<Long>?): List<Site>
    fun addRating(site: Site, user: User, rating: Float)
}