package de.othr.archeologicalfieldwork.model

import java.util.*

data class UserSpecificData(var visitedSites : MutableMap<String, Date> = HashMap(),
                            var favoriteSites : MutableList<String> = ArrayList())
