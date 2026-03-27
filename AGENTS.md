# AGENTS.md

## Scope
- Applies to the entire repository at `FlashLight/` (single app module: `:app`).
- This project is currently an Android Studio template baseline, not yet a flashlight feature implementation.

## Stack And Build System
- Kotlin + Jetpack Compose + Material3 in `app/src/main/java/com/gul/flashlight/`.
- Gradle Kotlin DSL with version catalog in `gradle/libs.versions.toml`.
- Android plugin and Compose plugin are declared at root in `build.gradle.kts` and applied in `app/build.gradle.kts`.
- Repositories are restricted by `settings.gradle.kts` (`RepositoriesMode.FAIL_ON_PROJECT_REPOS`).

## Module Map And Entry Flow
- Launcher activity is `com.gul.flashlight.MainActivity` declared in `app/src/main/AndroidManifest.xml`.
- UI entry flow is `MainActivity.onCreate()` -> `setContent` -> `FlashLightTheme` -> `Scaffold` -> `Greeting` in `app/src/main/java/com/gul/flashlight/MainActivity.kt`.
- Theme system lives in `app/src/main/java/com/gul/flashlight/ui/theme/` (`Theme.kt`, `Color.kt`, `Type.kt`).
- `FlashLightTheme` enables dynamic color on Android 12+ and falls back to static palettes.

## Conventions Specific To This Repo
- Package naming is `com.gul.flashlight` and subpackage `ui.theme`; keep new files consistent.
- Compose functions are top-level functions (e.g., `Greeting`, `GreetingPreview`) rather than fragment/view classes.
- XML resources are minimal and mainly app metadata (`res/values/strings.xml`, `res/values/themes.xml`).
- Java 11 compatibility is explicitly configured in `app/build.gradle.kts`; match this for added JVM code.
- Keep dependency versions centralized in `gradle/libs.versions.toml` before wiring in module dependencies.

## Developer Workflows
- Windows local build: `./gradlew.bat :app:assembleDebug`.
- Unit tests (JVM): `./gradlew.bat :app:testDebugUnitTest`.
- Instrumented tests (device/emulator required): `./gradlew.bat :app:connectedDebugAndroidTest`.
- Lint: `./gradlew.bat :app:lintDebug`.
- Full verification commonly used before merge: `./gradlew.bat :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`.

## Testing Reality (Current State)
- Only template tests exist: `ExampleUnitTest` and `ExampleInstrumentedTest`.
- There are no Compose UI tests for app behavior yet, so UI changes should add targeted tests when practical.

## Integration Boundaries And Gotchas
- No camera/torch permissions or CameraManager integration currently exist in `AndroidManifest.xml` or Kotlin sources.
- If adding flashlight functionality, permission/service wiring will require coordinated updates to Manifest + activity/logic code.
- Do not edit generated or transient outputs under `app/build/`.
- Respect ignored local config files (`local.properties`, `.idea/*`) listed in `.gitignore`.

