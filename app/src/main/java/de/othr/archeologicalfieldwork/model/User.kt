package de.othr.archeologicalfieldwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class User(
    var id: Long = 0,
    var email : String = "",
    var password : String = "",
    var visitedSites : MutableMap<Long, Date>
) : Parcelable