[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-orange)

# AppUtilX

> Lightweight, production-ready Android utility library with essential Kotlin and Flutter helpers.

AppUtilX centralizes common Android tasks — network checks, haptics/vibration, audio feedback, display metrics, biometric authentication, secure intents, clipboard, notifications, file management, and device info — into a single, clean API.

🌐 **Documentation:** [apputilx.mohamedzaitoon.com](https://apputilx.mohamedzaitoon.com)  
📖 **العربية:** [README.ar.md](README.ar.md)

---

## 📦 Installation & Setup

### 1. Add Dependency

```kotlin
// Add in gradle/libs.versions.toml
[versions]
apputilx = "1.5.0-beta01"

[libraries]
apputilx = { group = "com.github.mohamed-zaitoon", name = "apputilx", version.ref = "apputilx" }

// Add in settings.gradle.kts
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

// Add in app/build.gradle.kts
dependencies {
    implementation(libs.apputilx)
}
```

### 2. Initialize in Application Class

#### Kotlin (Android Native)
```kotlin
import android.app.Application
import apputilx.Utils

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.initialize(this)
    }
}
```

#### Flutter (Android Host `MyApp.kt`)
```kotlin
import android.app.Application
import apputilx.Utils

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.initialize(this)
    }
}
```

---

## 📄 License

Copyright (c) 2025–2027 Mohamed Zaitoon.
All rights reserved.
