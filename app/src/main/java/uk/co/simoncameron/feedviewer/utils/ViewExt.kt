package uk.co.simoncameron.feedviewer.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

fun ImageView.load(url: String) {

    val requestOptions = RequestOptions()
        .dontTransform()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()


    Glide.with(this)
        .setDefaultRequestOptions(requestOptions)
        .load(url)
        .into(this)

}