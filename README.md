# 📚 Unipi Audio Stories 🎧📱
## 📖Description
**Unipi Audio Stories** is an Android application designed to offer a fun and interactive storytelling experience for children. It reads **fairy tales** aloud using the mobile device’s speech engine, while displaying beautiful illustrations and storing listening preferences and statistics. Developed as part of a university project at the **University of Piraeus**. 🎓

### ✨Features:
- **Visuals**: Each story is accompanied by a charming, child-friendly illustration.
- **Firebase Integration**: Stories are fetched and synced in Firebase Console in Firestore Database and Authentication for the user's registration.
- **Image Management**: Images are stored locally in the app resources and referenced via a local database..
- **Simple UI**: Kid-friendly and intuitive interface design with accessible navigation.

- **Main Menu**: A central screen where users can choose from a list of available stories.
- **Story Playback**: Each story includes text, author name, year, and a relevant image, which is displayed while the story is read aloud.
- **Text-to-Speech Integration**: Children can listen to stories read aloud using Android's built-in Text-to-Speech engine.
- **Multilingual Interface**: The app interface supports English, German and Italian only for the available options, not for the stories using strings.xml localization.
- **Statistics Screen**: A dedicated screen that shows how many times users have listened to stories and which are theis favorites.

### 🛠️Implementation Details:
- Built using **Android Studio** with **Java** for development.
- **Firebase Console** (Firestore Database & Authentication)
- **SQLite** (local image references)
- **Text-to-Speech** (TTS) API
- **Multi-language** support via string resources
- **Activities & Navigation**: Each section (menu, story, stats) is managed via separate Activities using Intents.

### 📌 How it Works:
1. **Launch the App**: Users start on the main screen, where they can select a story through a Recycler View. 
2. **Select & Listen**: To read and listen to a story the user must create an account (email & password). After completing user's registration, the user can choose a story. Upon choosing a story, the app displays the content and starts narration using TTS. An image is shown alongside.
3.**Track Stats**: A separate screen allows users to track their story activity and favorite stories.
