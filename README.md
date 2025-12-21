[![](https://jitpack.io/v/mohamed-zaitoon/apputils.svg)](https://jitpack.io/#mohamed-zaitoon/apputils)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-Alpha-orange)
![Version](https://img.shields.io/badge/Version-25.12.1--blue)

# AppUtils

> 🚧 Alpha release — APIs may change before the stable version.

AppUtils is a lightweight Android utility library that centralizes the most commonly used helpers into a single, clean, and easy-to-use API.

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
    implementation 'com.github.mohamed-zaitoon:apputils:25.12.1'
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
    implementation("com.github.mohamed-zaitoon:apputils:25.12.1")
}
```

## ProGuard / R8

```proguard

-keep class hrm.widget.** { *; }

```

## Changelog

25.12.21-alpha — Initial Alpha Release
- Added core AppUtils initialization and lifecycle tracking
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
