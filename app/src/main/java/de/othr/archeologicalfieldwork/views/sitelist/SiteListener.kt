package de.othr.archeologicalfieldwork.views.sitelist

import de.othr.archeologicalfieldwork.model.Site

interface SiteListener {

    fun onSiteClick(site: Site)
}