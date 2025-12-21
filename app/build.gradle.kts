import java.io.FileOutputStream
import java.util.Calendar
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 36
    buildToolsVersion = "36.1.0"
    namespace = "mz.example"

    defaultConfig {
        applicationId = "mz.example"
        minSdk = 24
        targetSdk = 36
        multiDexEnabled = true
        
        
          // ===== versionCode =====
        val cal = Calendar.getInstance()
        val y = String.format(Locale.US, "%02d", cal.get(Calendar.YEAR) % 100)
        val m = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1)
        val d = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
        val h = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
        val lastDigit = cal.get(Calendar.MINUTE) % 10

        val versionCodeStr = y + m + d + h + lastDigit
        var generatedVersionCode = versionCodeStr.toInt()

        val oldVersionCodeFromFile = 251221186
        if (generatedVersionCode <= oldVersionCodeFromFile) {
            generatedVersionCode = oldVersionCodeFromFile + 1
        }

        versionCode = generatedVersionCode

        val last3 = versionCode.toString().takeLast(3)
        versionName = String.format(Locale.US, "%s.%s.%s.%s", y, m, d, last3)
        
    }

    

    buildTypes {
        getByName("debug") {
            isDebuggable = true
            // signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }
        getByName("release") {
            // signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
}

/* =========================
   version.properties
   ========================= */

val versionFile = rootProject.file("version.properties")

tasks.register("generateVersionProperties") {
    doLast {
        val props = Properties()
        props["versionCode"] = android.defaultConfig.versionCode.toString()
        props["versionName"] = android.defaultConfig.versionName.toString()

        versionFile.parentFile.mkdirs()
        FileOutputStream(versionFile).use {
            props.store(it, "Generated version properties")
        }
        println("✅ version.properties generated at: ${versionFile.absolutePath}")
    }
}

tasks.named("preBuild") {
    dependsOn("generateVersionProperties")
}

/* =========================
   Update oldVersionCode
   ========================= */

tasks.register("updateOldVersionCode") {
    doLast {
        val gradleFile = rootProject.file("app/build.gradle.kts")
        if (!versionFile.exists() || !gradleFile.exists()) return@doLast

        val props = Properties().apply {
            load(versionFile.inputStream())
        }
        val newCode = props["versionCode"] ?: return@doLast

        val updated = gradleFile.readText().replace(
            Regex("""val\s+oldVersionCodeFromFile\s*=\s*\d+"""),
            "val oldVersionCodeFromFile = $newCode"
        )
        gradleFile.writeText(updated)

        println("✅ oldVersionCodeFromFile updated to $newCode")
    }
}

tasks.whenTaskAdded {
    if (name == "assembleDebug" || name == "assembleRelease") {
        finalizedBy("updateOldVersionCode")
    }
}



dependencies {
    implementation(project(":library"))

     implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0-alpha08")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.multidex:multidex:2.0.1")
}



/* =========================
   Export APK (Safe – Gradle 9)
   ========================= */


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        allWarningsAsErrors.set(false)
        freeCompilerArgs.addAll(
            "-nowarn",                 // تجاهل كل warnings
            "-Xsuppress-version-warnings"
        )
    }
}


val customApkDir = layout.buildDirectory.dir("publish")

tasks.register<Copy>("exportReleaseApk") {
    from(layout.buildDirectory.dir("outputs/apk/release"))
    include("*.apk")
    rename {
        "${android.defaultConfig.applicationId}-${android.defaultConfig.versionName}-release.apk"
    }
    into(customApkDir)
}

tasks.matching { it.name == "assembleRelease" }.configureEach {
    finalizedBy("exportReleaseApk")
}
