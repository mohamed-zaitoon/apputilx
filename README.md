[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-Alpha-orange)
![Version](https://img.shields.io/badge/Version-1.0.0-alpha--blue)

# apputilx

> 🚧 Alpha release — APIs may change before the stable version.

apputilx is a lightweight Android utility library that centralizes the most commonly used helpers into a single, clean, and easy-to-use API.

Documents:
https://apputils.mohamed-zaitoon.com/

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

Groovy:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.mohamed-zaitoon:apputilx:1.0.0-alpha'
}
```

Kotlin DSL:
```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.mohamed-zaitoon:apputilx:1.0.0-alpha")
}
```

## ProGuard / R8

```proguard

-keep class apputilx.widget.** { *; }

```

## Changelog

25.12.21-alpha — Initial Alpha Release
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
