package de.othr.archeologicalfieldwork.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Rating(
    var userRating: MutableMap<String, Float> = HashMap(),
    var rating : Float = 0.0f
) : Parcelable