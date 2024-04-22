package edu.utap.DamiKitz.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import edu.utap.DamiKitz.R
import java.io.IOException

class LoadGlide(val context: Context){
    // Load a product image into an ImageView
    fun loadProductImage(image: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(image)
                .centerCrop()
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Load a user profile picture into an ImageView
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            Glide.with(context)
                .load(image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_placeholder)
                .into(imageView)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
