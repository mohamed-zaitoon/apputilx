[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-orange)

# AppUtilX

> Enterprise-grade, AndroidX-style Android & Flutter utility library under `com.mohamedzaitoon.apputilx.core.*`.

AppUtilX provides clean, modular Android category helpers — Network, Audio, Vibration, Display, Biometrics, Secure Intents, Clipboard, Notifications, File Management, Device/Battery info, and Cryptography — under `com.mohamedzaitoon.apputilx.core.*`.

🌐 **Documentation:** [apputilx.mohamedzaitoon.com](https://apputilx.mohamedzaitoon.com)  
📖 **العربية:** [README.ar.md](README.ar.md)

---

## 📦 Installation & Setup

### 1. Add Dependency (libs.versions.toml)

```toml
[versions]
apputilx = "1.5.0-beta02"

[libraries]
apputilx = { group = "com.github.mohamed-zaitoon", name = "apputilx", version.ref = "apputilx" }
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
import com.mohamedzaitoon.apputilx.core.AppUtilX

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
```

---

## 🚀 Category Usage Examples (AndroidX Style)

```kotlin
import com.mohamedzaitoon.apputilx.core.net.Network
import com.mohamedzaitoon.apputilx.core.hardware.Audio
import com.mohamedzaitoon.apputilx.core.hardware.Vibration
import com.mohamedzaitoon.apputilx.core.content.Clipboard
import com.mohamedzaitoon.apputilx.core.hardware.Device
import com.mohamedzaitoon.apputilx.core.hardware.Battery

// Network checks
val isOnline = Network.isConnected
val transport = Network.activeTransport()

// Audio & Haptics
Audio.playClickSound()
Vibration.vibrate(200)

// Clipboard
Clipboard.copyText("Hello AppUtilX")

// Device & Battery Info
val device = Device.deviceName()
val battery = Battery.getBatteryLevel()
```

---

## 📄 License

Copyright (c) 2025–2027 Mohamed Zaitoon.
All rights reserved.
