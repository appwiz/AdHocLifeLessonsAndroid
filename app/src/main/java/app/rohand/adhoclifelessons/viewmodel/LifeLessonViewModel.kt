package app.rohand.adhoclifelessons.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.rohand.adhoclifelessons.repository.LifeLessonRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LifeLessonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LifeLessonRepository(application)

    private val _uiState = MutableStateFlow(LifeLessonUiState())
    val uiState: StateFlow<LifeLessonUiState> = _uiState.asStateFlow()

    init {
        // Load stored lesson and start observing changes
        observeStoredData()
        // Start automatic updates
        startAutomaticUpdates()
        // Fetch initial lesson
        refreshLesson()
    }

    private fun observeStoredData() {
        viewModelScope.launch {
            combine(
                repository.getStoredLessonFlow(),
                repository.getLastUpdateTimeFlow()
            ) { lesson, lastUpdate ->
                lesson to lastUpdate
            }.collect { (lesson, lastUpdate) ->
                _uiState.value = _uiState.value.copy(
                    lesson = lesson,
                    lastUpdateTime = lastUpdate,
                    lastUpdateText = formatLastUpdate(lastUpdate)
                )
            }
        }
    }

    private fun startAutomaticUpdates() {
        viewModelScope.launch {
            while (isActive) {
                delay(60 * 60 * 1000) // 1 hour
                refreshLesson()
            }
        }
    }

    fun refreshLesson() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.fetchLifeLesson()
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    private fun formatLastUpdate(lastUpdateTime: Long?): String {
        if (lastUpdateTime == null) return "Never updated"

        val now = System.currentTimeMillis()
        val diffInMillis = now - lastUpdateTime

        return when {
            diffInMillis < 60 * 1000 -> "Just now"
            diffInMillis < 60 * 60 * 1000 -> {
                val minutes = diffInMillis / (60 * 1000)
                "$minutes minute${if (minutes == 1L) "" else "s"} ago"
            }
            diffInMillis < 24 * 60 * 60 * 1000 -> {
                val hours = diffInMillis / (60 * 60 * 1000)
                "$hours hour${if (hours == 1L) "" else "s"} ago"
            }
            else -> {
                val days = diffInMillis / (24 * 60 * 60 * 1000)
                "$days day${if (days == 1L) "" else "s"} ago"
            }
        }
    }
}

data class LifeLessonUiState(
    val lesson: String = "Love Each Other",
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastUpdateTime: Long? = null,
    val lastUpdateText: String = "Never updated"
)
