package edu.utap.DamiKitz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoldProduct(
    val userId: String = "",
    val title: String = "",
    val price: String = "",
    val quantitySold: String = "",
    val image: String = "",
    val orderId: String = "",
    val orderDate: Long = 0L,
    val subtotal: String = "",
    val shippingCost: String = "",
    val totalCost: String = "",
    val address: Address = Address(),
    var id: String = "",
) : Parcelable