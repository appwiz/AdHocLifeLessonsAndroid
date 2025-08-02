package app.rohand.adhoclifelessons.network

import app.rohand.adhoclifelessons.data.LifeLessonResponse
import retrofit2.Response
import retrofit2.http.GET

interface LifeLessonApiService {
    @GET("quote.json")
    suspend fun getLifeLesson(): Response<LifeLessonResponse>
}
