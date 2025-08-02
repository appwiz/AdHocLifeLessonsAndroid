# AdHocLifeLessons

A modern Android app built with Kotlin and Jetpack Compose that delivers inspiring life lessons to users. The app fetches wisdom from a remote API and presents it in a clean, visually appealing interface that works seamlessly across phones and tablets in any orientation.

## 📱 Features

- **Daily Wisdom**: Fetches life lessons from a remote API every hour
- **Offline Support**: Displays cached lessons when network is unavailable
- **Auto-Updates**: Background refresh every hour to get the latest content
- **Manual Refresh**: User-controlled refresh button for immediate updates
- **Responsive Design**: Works on phones and tablets in any orientation
- **Clean UI**: Large, centered, scrollable text with Material 3 design
- **Persistent Storage**: Saves lessons locally using DataStore

## 🏗️ Architecture

The app follows MVVM (Model-View-ViewModel) architecture with:

- **Jetpack Compose** for modern UI
- **Retrofit + OkHttp** for networking
- **DataStore** for local storage
- **Coroutines** for asynchronous operations
- **Material 3** design system

## 🎯 How It Works

### API Integration
The app connects to `https://rohand.app/lifelesson/quote.json` which returns:
```json
{
  "ts": "20250731",
  "v": 1,
  "s": "Life is Happy"
}
```

- `ts`: Timestamp when lesson was last updated
- `v`: Version of the lesson
- `s`: The actual lesson text (displayed to user)

### Storage Strategy
- **Primary**: Always try to fetch from API
- **Fallback**: Use last cached lesson if API unavailable
- **Default**: Show "Love Each Other" if no lesson ever cached

## 📊 Sequence Diagrams

### App Launch & Message Retrieval

```mermaid
sequenceDiagram
    participant U as User
    participant A as App
    participant VM as ViewModel
    participant R as Repository
    participant DS as DataStore
    participant API as Remote API

    U->>A: Launch App
    A->>VM: Initialize ViewModel
    VM->>R: observeStoredData()
    R->>DS: getStoredLessonFlow()
    DS-->>R: Return cached lesson
    R-->>VM: Emit cached lesson
    VM-->>A: Update UI State
    A-->>U: Display cached lesson

    Note over VM: Start automatic updates
    VM->>R: fetchLifeLesson()
    R->>API: GET /quote.json
    
    alt API Success
        API-->>R: Return lesson JSON
        R->>DS: saveLesson(lesson, timestamp)
        DS-->>R: Lesson saved
        R-->>VM: Success result
        VM-->>A: Update UI with new lesson
        A-->>U: Display fresh lesson
    else API Failure
        API-->>R: Network error
        R-->>VM: Failure result
        VM-->>A: Show error (keep cached lesson)
        A-->>U: Display cached lesson + error
    end
```

### Manual Refresh Flow

```mermaid
sequenceDiagram
    participant U as User
    participant A as App UI
    participant VM as ViewModel
    participant R as Repository
    participant DS as DataStore
    participant API as Remote API

    U->>A: Tap Refresh Button
    A->>VM: refreshLesson()
    VM->>A: Set loading state
    A-->>U: Show loading indicator

    VM->>R: fetchLifeLesson()
    R->>API: GET /quote.json

    alt API Success
        API-->>R: Return new lesson
        R->>DS: saveLesson(lesson, timestamp)
        DS-->>R: Save complete
        R-->>VM: Success with lesson data
        VM->>A: Update UI (lesson + timestamp)
        A-->>U: Display new lesson
    else API Failure
        API-->>R: Error response
        R-->>VM: Failure result
        VM->>A: Update UI (error + keep old lesson)
        A-->>U: Show error message
    end

    VM->>A: Clear loading state
    A-->>U: Hide loading indicator
```

### Automatic Background Updates

```mermaid
sequenceDiagram
    participant VM as ViewModel
    participant R as Repository
    participant DS as DataStore
    participant API as Remote API
    participant UI as User Interface

    Note over VM: Every 1 hour
    loop Automatic Update Cycle
        VM->>VM: delay(1 hour)
        VM->>R: fetchLifeLesson()
        R->>API: GET /quote.json
        
        alt New Lesson Available
            API-->>R: Return updated lesson
            R->>DS: saveLesson(newLesson, newTimestamp)
            DS-->>R: Storage complete
            R-->>VM: Success result
            Note over DS: Triggers Flow emission
            DS->>R: Emit new lesson via Flow
            R->>VM: Update UI state
            VM->>UI: Refresh display
        else No Changes or Error
            API-->>R: Same lesson or error
            R-->>VM: No update needed
            Note over VM: Continue with cached lesson
        end
    end
```

## 🔧 Technical Implementation

### Key Components

1. **LifeLessonViewModel**: Manages app state and coordinates data flow
2. **LifeLessonRepository**: Handles API calls and local storage
3. **LifeLessonApiService**: Retrofit interface for network requests
4. **LifeLessonScreen**: Jetpack Compose UI with lesson display
5. **DataStore**: Encrypted local storage for lessons and timestamps

### Dependencies

- `androidx.compose.*` - Modern UI toolkit
- `retrofit2` - Type-safe HTTP client
- `androidx.datastore` - Data storage solution
- `kotlinx.coroutines` - Asynchronous programming
- `androidx.lifecycle` - Lifecycle-aware components

## 🚀 Getting Started

### Prerequisites
- Android Studio Koala or later
- Android SDK API 28+ (Android 9.0)
- Kotlin 2.0.21+

### Building the App

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle dependencies
4. Run the app:
   ```bash
   ./gradlew app:assembleDebug
   ```

### Installation
The app supports Android 9.0 (API 28) and above, working on:
- Android phones (any screen size)
- Android tablets
- Both portrait and landscape orientations

## 📱 User Experience

### Main Screen
- **Large Text**: Life lesson displayed in readable, large font
- **Scrollable**: Text scrolls if it exceeds screen height
- **Centered**: Content perfectly centered for optimal readability
- **Material 3**: Modern design with proper theming

### Footer
- **Last Updated**: Shows human-readable time since last update
- **Refresh Button**: Manual refresh capability
- **Always Visible**: Footer stays at bottom of screen

### States
- **Loading**: Shows progress indicator during refresh
- **Online**: Displays fresh content from API
- **Offline**: Shows cached content with appropriate messaging
- **Error**: Graceful error handling with fallback content

## 🔒 Privacy & Storage

- **Local Only**: All data stored locally on device
- **No Sync**: No cloud synchronization or cross-device sharing
- **Minimal Data**: Only stores lesson text and timestamp
- **No Personal Info**: App doesn't collect user data

## 📄 License

This project is built as a demonstration of modern Android development practices using Jetpack Compose and follows Material Design guidelines.
