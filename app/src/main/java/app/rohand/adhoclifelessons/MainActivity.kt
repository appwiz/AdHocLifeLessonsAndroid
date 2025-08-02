package app.rohand.adhoclifelessons

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import app.rohand.adhoclifelessons.ui.LifeLessonScreen
import app.rohand.adhoclifelessons.ui.theme.AdHocLifeLessonsTheme
import app.rohand.adhoclifelessons.viewmodel.LifeLessonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdHocLifeLessonsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LifeLessonViewModel = viewModel()
                    val uiState by viewModel.uiState.collectAsState()

                    LifeLessonScreen(
                        uiState = uiState,
                        onRefresh = { viewModel.refreshLesson() }
                    )
                }
            }
        }
    }
}