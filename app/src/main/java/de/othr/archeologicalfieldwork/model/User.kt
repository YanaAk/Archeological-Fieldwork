package de.othr.archeologicalfieldwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class User(
    var id: String = "",
    var email : String = "",
    var password : String = "",
    var visitedSites : MutableMap<String, Date>,
    var favoriteSites : MutableList<String>
) : Parcelable