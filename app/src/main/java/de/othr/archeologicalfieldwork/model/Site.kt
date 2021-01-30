package de.othr.archeologicalfieldwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Site(
    var id: String = "",
    var name : String = "",
    var description : String = "",
    var images : MutableList<String> = ArrayList(),
    var notes : String = "",
    var location : Location = Location(),
    var rating : Rating = Rating()
) : Parcelable
