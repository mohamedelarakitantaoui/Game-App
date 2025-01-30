# Quiz Game 

## Overview

Guizgame is an Android game developed using Kotlin and Gradle. The project includes build automation with Gradle and is structured for efficient development and deployment.

## Features

- Android game built with Kotlin
- Gradle-based project structure
- Multithreaded functionality using Kotlin Coroutines
- Firebase integration for secure login and signup
- Easy setup and deployment

## Prerequisites

Ensure you have the following installed before running the project:

- [Android Studio](https://developer.android.com/studio)
- [JDK 11+](https://www.oracle.com/java/technologies/javase-downloads.html)
- Gradle (if not using Android Studio's bundled Gradle)
- Git (for version control)

## Installation

Follow these steps to set up the project:

1. Clone the repository:
   ```sh
   git clone https://github.com/mohamedelarakitantaoui/game-app.git
   ```
2. Navigate to the project directory:
   ```sh
   cd GameProject2
   ```
3. Open the project in Android Studio.
4. Sync Gradle files and install dependencies.
5. Run the application on an emulator or physical device.

## Build and Run

To build and run the project manually:

```sh
./gradlew assembleDebug   # For Linux/macOS
```

or

```sh
gradlew.bat assembleDebug   # For Windows
```

Then install the generated APK on your device.

## Multithreading with Coroutines

This project utilizes Kotlin Coroutines for efficient multithreading, allowing smooth execution of background tasks without blocking the main UI thread.

## Firebase Integration

GameProject2 integrates Firebase for user authentication, providing secure login and signup functionality. This ensures a seamless and reliable authentication process for users.

## Contributing

Feel free to fork the repository and submit pull requests with improvements or bug fixes.

## Contact

For any issues or suggestions, open an issue on the [GitHub repository](https://github.com/mohamedelarakitantaoui/game-app/issues).

