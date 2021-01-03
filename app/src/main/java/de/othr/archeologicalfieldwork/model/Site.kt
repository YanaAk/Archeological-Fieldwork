package de.othr.archeologicalfieldwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Site(
    var id: Long = 0,
    var name : String = "",
    var description : String = "",
    var images : List<String> = ArrayList()
) : Parcelable
