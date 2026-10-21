#!/usr/bin/env bash
set -euo pipefail
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle is not installed. Install Gradle 8.9 and JDK 17, then run this script again." >&2
  exit 2
fi
if [[ -z "${ANDROID_HOME:-}" && -z "${ANDROID_SDK_ROOT:-}" ]]; then
  echo "ANDROID_HOME or ANDROID_SDK_ROOT must point to an installed Android SDK (platform 35)." >&2
  exit 2
fi
gradle --no-daemon --stacktrace :app:assembleDebug
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
