# Speech to Text Translator – Android App (ML Kit + Translation API)

## 1. Overview
This project is an Android application that performs real-time speech-to-text transcription and translates the recognized text into another language. The application integrates speech recognition, translation services, and a graphical user interface for user interaction.

## 2. Features
- Start and stop voice recording
- Convert speech to text in real-time
- Translate recognized text into the selected language
- Display original and translated text
- Allow users to edit the original text manually
- Select target language (Vietnamese, Spanish, French, German, Japanese, etc.)
- Request microphone permission
- Error handling for speech recognition and network issues

## 3. Technology Stack
- Android Studio / Kotlin
- Android SpeechRecognizer (Google Speech-to-Text)
- Retrofit & Coroutines for API calls
- MyMemory Translation API

## 4. UI Components
- Button: Start/Stop Recording
- EditText: Original Speech Text (Editable)
- Button: Translate (Manual trigger)
- TextView: Translated Text
- Spinner: Language Selection
- Status Indicator: Recording / Idle

## 5. Development Steps
1. Initialized Android project with Kotlin
2. Implemented microphone permission handling
3. Integrated Android's `SpeechRecognizer` for real-time transcription
4. Integrated Retrofit with `MyMemory` translation API
5. Designed and optimized the UI with Material Design
6. Added manual editing and manual translation buttons
