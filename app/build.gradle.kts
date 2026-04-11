import java.io.ByteArrayOutputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val majorRelease: Int = 0

val commitCountOutput = ByteArrayOutputStream()
exec {
    commandLine("git", "rev-list", "--count", "HEAD")
    workingDir = rootDir
    standardOutput = commitCountOutput
}
val commitCount = commitCountOutput.toString().trim().toInt()

val buildNumberFile = file("build_number.txt")
val existingLine = if (buildNumberFile.exists()) buildNumberFile.readLines().firstOrNull() else null
val parts = existingLine?.split(".")
val parsedMinor = parts?.getOrNull(0)?.toIntOrNull()
val parsedBuild = parts?.getOrNull(1)?.toIntOrNull()

var minorRelease: Int
var buildNumber: Int
if (parsedMinor == null || parsedBuild == null || parsedMinor != commitCount) {
    minorRelease = commitCount
    buildNumber = 0
} else {
    minorRelease = parsedMinor
    buildNumber = parsedBuild + 1
}

val projectName = rootDir.name

android {
    namespace = "com.example.intervalcompanion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.intervalcompanion"
        minSdk = 26
        targetSdk = 35
        versionCode = "$minorRelease${buildNumber.toString().padStart(2, '0')}".toInt()
        versionName = "$majorRelease.$minorRelease.$buildNumber"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "${projectName}_$majorRelease.$minorRelease.$buildNumber.apk"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.datastore:datastore-preferences:1.1.2")
    implementation("com.google.code.gson:gson:2.11.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
}

tasks.register("generateReleaseNotes") {
    doLast {
        buildNumberFile.writeText("$minorRelease.$buildNumber\n")

        val logOutput = ByteArrayOutputStream()
        exec {
            commandLine("git", "log", "--pretty=format:%ad %s", "--date=format:%Y-%m-%d %H:%M", "-n", "10")
            workingDir = rootDir
            standardOutput = logOutput
        }

        val assetsDir = file("src/main/assets")
        if (!assetsDir.exists()) assetsDir.mkdirs()

        file("src/main/assets/release_notes.txt").writeText(
            "Release $majorRelease.$minorRelease.$buildNumber\n\n${logOutput.toString().trim()}\n"
        )
    }
}

tasks.named("preBuild") {
    dependsOn("generateReleaseNotes")
}
