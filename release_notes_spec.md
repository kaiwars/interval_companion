# Setting the Stage

In the `app` subdirectory there is a file called `build_number.txt`.

It contains a string in the format `minorRelease.buildNumber` (e.g. `47.3`).

In the same subdirectory is the build file `build.gradle.kts`.

It contains a top-level `val majorRelease: Int` which is updated manually when a major release is made.

`build.gradle.kts` must import `java.io.ByteArrayOutputStream` to capture git command output.


# Version Computation (Configuration Time)

The following logic runs at **configuration time** — at the top level of `build.gradle.kts`, outside any task. This is required because the results feed into `android.defaultConfig` and APK naming, which Gradle resolves during configuration.

## Read commitCount from git

Run:
```
commandLine("git", "rev-list", "--count", "HEAD")
workingDir = rootDir
```
Parse the output as an Int: `commitCount`.

## Read and update build_number.txt

Read `minorRelease` and `buildNumber` from `build_number.txt` using a relative path (`file("build_number.txt")`), which resolves correctly since `build.gradle.kts` is in the same `app/` directory.

If the file does not exist, or does not contain a valid `minorRelease.buildNumber` on the first line, or `minorRelease` is not equal to `commitCount`:
- Set `minorRelease = commitCount`
- Set `buildNumber = 0`

If `minorRelease` equals `commitCount` (same commit, rebuilding):
- Set `buildNumber = buildNumber + 1`

(The file is **not** written here — writing happens inside the task at execution time.)

## Set version fields

```
android.defaultConfig.versionCode = "$minorRelease${buildNumber.toString().padStart(2, '0')}".toInt()
android.defaultConfig.versionName = "$majorRelease.$minorRelease.$buildNumber"
```

The version code is the numeric concatenation of `minorRelease` (as-is) and `buildNumber` zero-padded to at least two digits (e.g. minor=47, build=3 → versionCode=4703).

## APK naming

the projectName is set from the root folder of this project

```
applicationVariants.all {
    outputs.all {
        outputFileName = "${projectName}_$majorRelease.$minorRelease.$buildNumber.apk"
    }
}
```


# generateReleaseNotes Task (Execution Time)

`generateReleaseNotes` is wired to `preBuild`:
```
tasks.named("preBuild") {
    dependsOn("generateReleaseNotes")
}
```

Inside `doLast {}` the task does the following:

## Write build_number.txt

Write `$minorRelease.$buildNumber` back to `build_number.txt`.
(`minorRelease` and `buildNumber` are the values already computed at configuration time.)

## Generate release notes

Set the header to `"Release $majorRelease.$minorRelease.$buildNumber"`.

Retrieve the last 10 commit messages from git:
```
commandLine("git", "log", "--pretty=format:%ad %s", "--date=format:%Y-%m-%d %H:%M", "-n", "10")
workingDir = rootDir
```

Create `src/main/assets/` if it does not exist.

Write `src/main/assets/release_notes.txt` containing the header, an empty line, and the commit messages.
