package de.othr.archeologicalfieldwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: Long = 0,
    var email : String = "",
    var password : String = ""
) : Parcelable