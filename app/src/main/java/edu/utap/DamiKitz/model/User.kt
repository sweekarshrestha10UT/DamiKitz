package edu.utap.DamiKitz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val profilePicture: String = "",
    val phoneNumber: Long = 0,
    val gender: String = "",
    val profileCompleted: Int = 0
) : Parcelable