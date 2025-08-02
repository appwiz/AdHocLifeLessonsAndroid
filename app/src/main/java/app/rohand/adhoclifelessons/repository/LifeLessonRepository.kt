package app.rohand.adhoclifelessons.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.rohand.adhoclifelessons.data.LifeLessonResponse
import app.rohand.adhoclifelessons.network.LifeLessonApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "life_lessons")

class LifeLessonRepository(private val context: Context) {

    private val api: LifeLessonApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://rohand.app/lifelesson/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LifeLessonApiService::class.java)
    }

    companion object {
        private val LESSON_KEY = stringPreferencesKey("lesson")
        private val TIMESTAMP_KEY = stringPreferencesKey("timestamp")
        private val LAST_UPDATE_KEY = stringPreferencesKey("last_update")
        private const val DEFAULT_LESSON = "Love Each Other"
    }

    suspend fun fetchLifeLesson(): Result<LifeLessonResponse> {
        return try {
            val response = api.getLifeLesson()
            if (response.isSuccessful && response.body() != null) {
                val lessonResponse = response.body()!!
                // Store the lesson locally
                saveLesson(lessonResponse.lesson, lessonResponse.timestamp)
                Result.success(lessonResponse)
            } else {
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveLesson(lesson: String, timestamp: String) {
        context.dataStore.edit { preferences ->
            preferences[LESSON_KEY] = lesson
            preferences[TIMESTAMP_KEY] = timestamp
            preferences[LAST_UPDATE_KEY] = System.currentTimeMillis().toString()
        }
    }

    suspend fun getStoredLesson(): String {
        return context.dataStore.data.first()[LESSON_KEY] ?: DEFAULT_LESSON
    }

    suspend fun getLastUpdateTime(): Long? {
        val lastUpdateString = context.dataStore.data.first()[LAST_UPDATE_KEY]
        return lastUpdateString?.toLongOrNull()
    }

    fun getStoredLessonFlow(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[LESSON_KEY] ?: DEFAULT_LESSON
        }
    }

    fun getLastUpdateTimeFlow(): Flow<Long?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_UPDATE_KEY]?.toLongOrNull()
        }
    }
}
