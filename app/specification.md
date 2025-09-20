### Introduction
AdHocLifeLessons is an Android app built with Kotlin and Jetpack Compose. It works in any orientation on Android phones and tablets. The app fetches a "life lesson" from a remote API and displays it in a visually appealing way. The lesson is updated every hour, and the app stores the last successfully retrieved lesson locally.

### How it works
- When the app is launched, it displays a "life lesson" centered in the main view. The lesson is retrieved from https://rohand.app/lifelesson/quote.json using a GET request.
  The JSON document has the following structure:
```json
{
  "ts": "20250731",
  "v": 1,
  "s": "Life is Happy"
}
```
- The `ts` field is a timestamp indicating when the lesson was last updated.
- The `v` field is the version of the lesson.
- The `s` field is the actual lesson text. Display this text in the app.

### Requirements
- If the API endpoint is unreachable, the app should display
    - either the last successfully retrieved lesson, or
    - a default lesson if no lesson has been retrieved before
- The default lesson is "Love Each Other".
- Display the lesson in a large font, centered in the view. This text should be scrollable if it exceeds the screen height. Make this text visually appealing, using a suitable font and color.

### Storage
- All information is stored locally in the app's storage. Nothing is synced across devices.
- The app should store the last successfully retrieved lesson and the timestamp of the last update in local storage.

### Header

- Add a small header with a separator line. The title in the header is "// life lessons //" as written and left aligned. make it look cool.
- adjust the layout. make sure you respect the visible view and the text is not overlapped by the device notch.

### Footer
The app view also contains a footer.
- Display a tiny footer at the bottom of the view that shows the last update time of the lesson in a human-readable format (e.g., "Last updated: 2 minutes ago"). The footer is always visible.
- display the quote date "quoted: September 15, 2025"
- The app should also have a button that allows the user to refresh the lesson by fetching it again from the API. Add this button to the footer.

### Automatic Updates
- The app should automatically fetch the latest lesson every 1 hour in the background. If there is a new message, the app should update the displayed lesson and the footer timestamp accordingly.
