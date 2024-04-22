package edu.utap.DamiKitz.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Order(
    val userId: String = "",
    val items: ArrayList<Cart> = ArrayList(),
    val address: Address = Address(),
    val title: String = "",
    val image: String = "",
    val subtotal: String = "",
    val shippingCost: String = "",
    val totalCost: String = "",
    var orderDate: Long = 0L,
    var id: String = "",
) : Parcelable