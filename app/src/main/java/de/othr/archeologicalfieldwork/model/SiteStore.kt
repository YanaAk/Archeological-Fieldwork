package de.othr.archeologicalfieldwork.model

interface SiteStore {
    fun findAll(): List<Site>
    fun findById(id: Long): Site?
    fun create(site: Site)
    fun update(site: Site)
    fun delete(site: Site)
}