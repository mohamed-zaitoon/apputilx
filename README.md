[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-orange)

# Android Helper

> Enterprise-grade, clean Android & Flutter utility library under `com.mohamedzaitoon.apputilx.*`.

Android Helper provides clean Android category helpers — Network, Audio, Vibration, Display, Biometrics, Secure Intents, Clipboard, Notifications, File Management, Device/Battery info, and Cryptography.

🌐 **Documentation:** [apputilx.mohamedzaitoon.com](https://apputilx.mohamedzaitoon.com)  
📖 **العربية:** [README.ar.md](README.ar.md)

---

## 📦 Installation & Setup

### 1. Add Dependency (libs.versions.toml)

```toml
[versions]
androidHelper = "1.5.0-beta04"

[libraries]
android-helper = { group = "com.github.mohamed-zaitoon", name = "android-helper", version.ref = "androidHelper" }
```

### 2. Add Repository (settings.gradle.kts)

```kotlin
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

### 3. Initialize in Application Class

```kotlin
import android.app.Application
import com.mohamedzaitoon.apputilx.AppUtilX

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
```

---

## 🚀 Category Usage Examples (Clean Package)

```kotlin
import com.mohamedzaitoon.apputilx.net.Network
import com.mohamedzaitoon.apputilx.hardware.Audio
import com.mohamedzaitoon.apputilx.hardware.Vibration
import com.mohamedzaitoon.apputilx.content.Clipboard
import com.mohamedzaitoon.apputilx.hardware.Device
import com.mohamedzaitoon.apputilx.hardware.Battery

// Network checks
val isOnline = Network.isConnected
val transport = Network.activeTransport()

// Audio & Haptics
Audio.playClickSound()
Vibration.vibrate(200)

// Clipboard
Clipboard.copyText("Hello Android Helper")

// Device & Battery Info
val device = Device.deviceName()
val battery = Battery.getBatteryLevel()
```

---

## 📄 License

Copyright (c) 2025–2027 Mohamed Zaitoon.
All rights reserved.
