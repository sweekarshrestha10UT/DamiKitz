package edu.utap.DamiKitz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val userId: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val zipCode: String = "",
    val deliveryInstructions: String = "",
    val type: String = "",
    val otherDetails: String = "",
    var id: String = "",
) : Parcelable