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
- Toast helper (deprecated for final 1.4.0) and notification utilities
- Network connectivity checks & listeners
- Lifecycle-aware network callbacks
- Clipboard utilities
- Keyboard utilities (show / hide / toggle / state detection)
- Vibration helpers
- Open URLs (Browser & Chrome Custom Tabs)
- Safe intent helpers (WhatsApp, dial, email, share, app settings)
- Screen capture control
- Notification utilities
- Biometric authentication helper
- App signature validation
- Storage & cache helpers
- Device & system information helpers

## Download

Kotlin DSL:
```kotlin
 // Add in gradle/libs.versions.toml
    apputilx = "1.4.0-beta01"
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

1.4.0-alpha01 — Android 17-beta ready
- Updated to versionCode 131 / versionName 1.4.0-alpha01
- Lifecycle-aware network callback to avoid leaks
- Safer vibration APIs (Q+ attributes fallback)
- Safe intent helpers and app settings shortcut
- Added biometric authentication helper
- Added ProcessLifecycle / Biometric dependencies
- NotificationCompat foreground service behavior on Android 14+
- Deprecated the Toast helper for the final 1.4.0 release; prefer Android Toast or Material Snackbar directly
- Replaced deprecated platform calls with modern APIs where possible, keeping guarded legacy fallbacks for older Android versions

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
