package uk.co.simoncameron.feedviewer.data.api

import io.reactivex.Single
import retrofit2.http.GET
import uk.co.simoncameron.feedviewer.data.dto.FeedResponse

interface ApiService {
    @GET("feed")
    fun getFeed() : Single<FeedResponse>
}