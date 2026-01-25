[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-orange)

# apputilx

> 🚧 Release — APIs may change before the stable version.

apputilx is a lightweight Android utility library that centralizes the most commonly used helpers into a single, clean, and easy-to-use API.

Documents:
https://apputilx.mohamedzaitoon.com/

Features:
- Toast & Snackbar helpers
- Network connectivity checks & listeners
- Clipboard utilities
- Keyboard utilities (show / hide / toggle / state detection)
- Vibration helpers
- Open URLs (Browser & Chrome Custom Tabs)
- Screen capture control
- Notification utilities
- App signature validation
- Device & system information helpers

## Download

Kotlin DSL:
```kotlin
 // Add in gradle/libs.versions.toml
    apputilx = "1.2.1"
    apputilx = { group = "com.github.mohamed-zaitoon" , name = "apputilx" , version.ref = "apputilx" }
    
    //Add in settings.gradle.kts
repositories {
    google()
    mavenCentral()
    maven {
       maven { url = uri("https://jitpack.io") }
    }
}

dependencies {
    implementation(libs.apputilx)
}
```

## ProGuard / R8

```proguard

-keep class apputilx.widget.** { *; }

```

## Changelog

1.0.3 — Initial Release
- Added core apputil initialization and lifecycle tracking
- Added Toast and Snackbar utilities
- Added network connectivity checks and listeners
- Added clipboard utilities
- Added keyboard utilities (show, hide, toggle, state detection)
- Added vibration helpers
- Added URL opening utilities with Chrome Custom Tabs
- Added screen capture blocking / unblocking
- Added notification helpers
- Added app signature validation
- Added device and system information utilities

## License

Copyright (c) 2025–2026 Mohamed Zaitoon.
All rights reserved.
