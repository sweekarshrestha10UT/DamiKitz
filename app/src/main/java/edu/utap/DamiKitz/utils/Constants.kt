package edu.utap.DamiKitz.utils

import android.app.Activity
import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap

// Constants object containing various application constants
object Constants {

    // Shared preferences keys
    const val SHOP_PREFERENCES: String = "ShopPreferences"
    const val CURRENT_NAME: String = "CurrentName"
    const val CURRENT_SURNAME: String = "CurrentSurname"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    // Function to get the file extension from a URI
    fun getFileExt(activity: Activity, uri: Uri?): String? {
        // Get the MIME type map
        val mimeTypeMap = MimeTypeMap.getSingleton()
        // Return the file extension based on the MIME type of the URI
        return mimeTypeMap.getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}
