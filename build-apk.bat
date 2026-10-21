@echo off
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle 8.9 is not installed or not on PATH.
  exit /b 2
)
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (
  echo Set ANDROID_HOME or ANDROID_SDK_ROOT to your Android SDK path.
  exit /b 2
)
gradle --no-daemon --stacktrace :app:assembleDebug
if errorlevel 1 exit /b %errorlevel%
echo APK: app\build\outputs\apk\debug\app-debug.apk
