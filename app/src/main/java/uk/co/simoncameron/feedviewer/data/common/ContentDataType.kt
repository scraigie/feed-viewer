package uk.co.simoncameron.feedviewer.data.common

import com.google.gson.annotations.SerializedName

enum class ContentDataType {
    @SerializedName("ADS") ADS,
    @SerializedName("NEWS") NEWS,
    @SerializedName("PREMIUM_NEWS") PREMIUM_NEWS
}