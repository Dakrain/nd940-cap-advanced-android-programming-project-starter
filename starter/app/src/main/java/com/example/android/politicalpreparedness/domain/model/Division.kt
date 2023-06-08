package com.example.android.politicalpreparedness.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Division(
    val id: String,
    val country: String,
    val state: String
) : Parcelable {
    fun getAddress(): String {
        return "$state, $country"
    }
}