package app.rohand.adhoclifelessons.data

import com.google.gson.annotations.SerializedName

data class LifeLessonResponse(
    @SerializedName("ts") val timestamp: String,
    @SerializedName("v") val version: Int,
    @SerializedName("s") val lesson: String
)
