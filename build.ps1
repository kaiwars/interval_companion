# build.ps1 - Android build only

$ErrorActionPreference = 'Continue'
$ProjectRoot = $PSScriptRoot

# Use Android Studio bundled JDK
$env:JAVA_HOME = "C:\Program Files\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

function Say($text = "") {
    Write-Output $text
}

Say "Using Java from: $env:JAVA_HOME"
cmd /c "java -version 2>&1" | ForEach-Object { Say $_ }

# ---------- helpers ---------------------------------------------------------

function Write-Header($text) {
    Say ""
    Say "=== $text ==="
}

# ---------- Android build ---------------------------------------------------

Write-Header 'Android Build  (gradlew assembleDebug)'

$gradlew = Join-Path $ProjectRoot 'gradlew.bat'
$androidErrors = 0

if (-not (Test-Path $gradlew)) {
    Say "  FAIL  $gradlew not found"
    $androidErrors = 1
} else {
    $androidOutput = & $gradlew --project-dir $ProjectRoot assembleDebug 2>&1
    $androidExit = $LASTEXITCODE

    if ($androidExit -eq 0) {
        Say '  OK  build succeeded'
    } else {
        $androidErrors = 1

        # Strip routine progress lines; keep meaningful diagnostics
        $noisePattern = '^> Task |^> Configure |^Starting a Gradle Daemon|^Deprecated Gradle|^\d+ actionable|^See the profiling|^To honour|^\s*$'
        $diagnostics = $androidOutput | Where-Object { $_ -notmatch $noisePattern }

        foreach ($line in $diagnostics) {
            Say "  $line"
        }

        Say "  Android build FAILED"
    }
}

# ---------- summary ---------------------------------------------------------

Write-Header 'Summary'

if ($androidErrors -eq 0) {
    Say '  All checks passed.'
} else {
    Say '  Build FAILED - see errors above.'
}

Say ""
exit $androidErrors