name: Build Android APK from ZIP

on:
  workflow_dispatch:
  push:
    branches: [ main, master ]

permissions:
  contents: read

jobs:
  build-debug-apk:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3

      - name: Install Android platform and build tools
        run: sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"

      - name: Extract project ZIP
        shell: bash
        run: |
          set -euo pipefail
          mkdir -p extracted-project
          ZIP_FILE="$(find . -maxdepth 2 -type f -iname '*.zip' ! -path './extracted-project/*' | head -n 1)"
          if [ -z "$ZIP_FILE" ]; then
            echo "No project ZIP found in the repository root. Upload the project ZIP first."
            exit 1
          fi
          echo "Extracting: $ZIP_FILE"
          unzip -q "$ZIP_FILE" -d extracted-project
          PROJECT_DIR="$(find extracted-project -type f -name settings.gradle.kts -print -quit | xargs -r dirname)"
          if [ -z "$PROJECT_DIR" ] || [ ! -f "$PROJECT_DIR/app/build.gradle.kts" ]; then
            echo "Could not find settings.gradle.kts and app/build.gradle.kts inside the ZIP."
            find extracted-project -maxdepth 4 -type f | head -n 80
            exit 1
          fi
          echo "PROJECT_DIR=$PROJECT_DIR" >> "$GITHUB_ENV"
          echo "Project root: $PROJECT_DIR"

      - name: Set up Gradle 8.9
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.9'

      - name: Build debug APK
        shell: bash
        run: gradle --no-daemon --stacktrace -p "$PROJECT_DIR" :app:assembleDebug

      - name: Upload APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: ProKitchenDesigner-debug-apk
          path: extracted-project/**/app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error
          retention-days: 14
