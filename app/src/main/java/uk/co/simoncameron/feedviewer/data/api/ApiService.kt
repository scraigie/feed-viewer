package uk.co.simoncameron.feedviewer.data.api

import io.reactivex.Single
import retrofit2.http.GET
import uk.co.simoncameron.feedviewer.data.dto.FeedDTO

interface ApiService {
    @GET("feed")
    fun getFeed() : Single<List<FeedDTO>>
}