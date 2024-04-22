package edu.utap.DamiKitz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cart(
    val userId: String = "",
    val productOwnerId: String = "",
    val productId: String = "",
    val title: String = "",
    val price: String = "",
    val image: String = "",
    var cartQuantity: String = "",
    var stockQuantity: String = "",
    var id: String = "",
) : Parcelable