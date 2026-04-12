# Interval Companion

Android app that plays recorded voice cues during interval training sessions.

## Tech Stack

- **Language:** Kotlin 2.1.0
- **UI:** Jetpack Compose + Material3 (BOM 2024.12.01)
- **Navigation:** Navigation Compose 2.8.4
- **Persistence:** DataStore Preferences 1.1.2 + Gson 2.11.0 (rounds serialized as JSON)
- **Audio:** MediaRecorder / MediaPlayer / AudioManager / LoudnessEnhancer
- **Min SDK:** 26 · Target SDK: 35 · Java 17

## Project Structure

```
app/src/main/java/com/example/intervalcompanion/
├── IntervalCompanionApp.kt       Application class; holds settingsRepository and audioEngine singletons
├── MainActivity.kt               Single activity; hosts Compose content
├── audio/
│   └── AudioEngine.kt            AudioFocus requests, sequential MediaPlayer playback, LoudnessEnhancer for volume boost, file paths
├── data/
│   ├── SettingsRepository.kt     DataStore access, all suspend mutators, settingsFlow
│   └── model/
│       ├── Round.kt              Round data class; activeIntervals() / hasAnyInterval() helpers
│       └── Settings.kt           AppSettings, AudioPosition enum, AudioFocusStrategy enum
├── navigation/
│   └── AppNavigation.kt          NavHost + ModalNavigationDrawer; Screen sealed class
└── ui/
    ├── theme/                    Material3 dynamic-color theme
    ├── go/                       GoScreen + GoViewModel (execution loop, timer)
    ├── help/                     HelpScreen (static; no ViewModel)
    ├── releasenotes/             ReleaseNotesScreen (static; reads release_notes.txt from assets; no ViewModel)
    └── settings/
        ├── SettingsHubScreen.kt  Settings main menu (list of sub-screens)
        ├── rounds/               Round CRUD
        ├── intervalnames/        Interval name fields
        ├── voiceplayback/        Audio position radio groups (before / after / don't play) + volume boost (dB)
        ├── voicerecording/       Record / playback UI; uses MediaRecorder
        └── audiofocus/           Duck vs. Pause-and-resume
```

## Architecture

**MVVM + Repository.** Each screen has a paired ViewModel. ViewModels access singletons via `(application as IntervalCompanionApp).settingsRepository` — no DI framework.

- ViewModels expose `StateFlow<UiState>` collected with `collectAsState()` in screens.
- All DataStore mutations are `suspend` functions launched from `viewModelScope`.
- `stateIn(SharingStarted.WhileSubscribed(5000L))` is the standard pattern for derived StateFlows.

## Navigation

```
Drawer: Go | Settings | Help
  Go          → GoScreen (start destination)
  Settings    → SettingsHubScreen
                  └─ Rounds / Interval Names / Voice Playback / Voice Recording / Audio Focus / Release Notes
                       └─ back → SettingsHubScreen → back → Go
  Help        → HelpScreen (also reachable via "?" button in every screen's TopAppBar)
```

Routes are `Screen` sealed-class objects in `AppNavigation.kt`.

## Key Data Models

```kotlin
AppSettings(
    rounds: List<Round>,
    intervalName1/2/3: String,          // defaults: "fast", "slow", "chill"
    intervalNamePosition: AudioPosition, // BEFORE | AFTER | DONT_PLAY
    roundNumberPosition: AudioPosition,
    audioFocusStrategy: AudioFocusStrategy, // DUCK | PAUSE_RESUME
    roundRecordingCount: Int,            // how many round-number clips to show
    volumeBoostDb: Float                 // 0–50 dB; applied via LoudnessEnhancer per MediaPlayer instance
)

Round(id, checked, interval1/2/3: Int?) // null or 0 = skip that interval slot
```

## Audio Files

Stored in `context.filesDir/audio/` as M4A:
- `interval_0.m4a`, `interval_1.m4a`, `interval_2.m4a`
- `round_0.m4a` … `round_N.m4a`

`AudioEngine.getAudioFile(type, index)` is the single point for resolving paths. `playFiles(files, volumeBoostDb)` silently skips files that don't exist; attaches a `LoudnessEnhancer` to each `MediaPlayer` when `volumeBoostDb > 0`.

## Execution Loop (GoViewModel)

- `play()` launches `runExecution()` in `viewModelScope`.
- `hasActiveRounds: StateFlow<Boolean>` is derived from `settingsFlow`; GoScreen uses it to show a red warning when no rounds are configured.
- Iterates active (checked) rounds round-robin until `stop()`.
- Settings are re-read at the start of each round so live changes take effect.
- `countdown(seconds, roundRemainingAtStart)` runs in 100 ms ticks; pauses when `PlayState.PAUSED`, returns `false` when stopped; updates `intervalRemainingSeconds` and `roundRemainingSeconds` each second. `runExecution()` computes `roundTotalSeconds` and tracks `roundSecondsConsumed` per interval to derive `roundRemainingAtStart`, and initialises both remaining fields in state before calling `countdown` so the display is correct from tick 0.
- Audio clips are built by `buildStartClips` / `buildEndClips` and passed to `AudioEngine`. DONT_PLAY falls through (no file added). Non-existent files are filtered by `AudioEngine`.

## Versioning & Build

`app/build.gradle.kts` computes version fields at **configuration time** (top-level, before `android {}`):

- `commitCount` = `git rev-list --count HEAD` → used as `minorRelease`
- `buildNumber` is read from `app/build_number.txt` (`minorRelease.buildNumber`); resets to 0 on a new commit, increments on each rebuild of the same commit
- `versionCode` = numeric concatenation of `minorRelease` + `buildNumber` zero-padded to 2 digits (e.g. 3.0 → 300)
- `versionName` = `majorRelease.minorRelease.buildNumber`
- `majorRelease` is a top-level `val` in `build.gradle.kts`, updated manually for major releases
- APK name: `${rootDir.name}_$majorRelease.$minorRelease.$buildNumber.apk`

The `generateReleaseNotes` task (wired to `preBuild`) writes `build_number.txt` and generates `src/main/assets/release_notes.txt` (version header + last 10 git commits).

## Conventions

- Private mutable state: `_foo: MutableStateFlow` exposed as `val foo: StateFlow`.
- UI state objects are immutable data classes; use `.copy()` for updates.
- Settings screens save on every `onValueChange` (not on focus-loss) to avoid data loss on back-navigation.
- `audioposition/` package exists but is unused — superseded by `voiceplayback/`.
