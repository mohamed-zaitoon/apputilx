[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-orange)

# AppUtilX (باللغة العربية)

> مكتبة أدوات وأدوات مساعدة خفيفة وموحدة مبنية طبقاً لمعايير تقسيم وحزم AndroidX لمشاريع أندرويد و فلاتر.

تجمع مكتبة **AppUtilX** جميع المهام المكررة في تطوير أندرويد — مثل فحص الشبكة، الاهتزاز الفعلي، الصوتيات والشاشة، المصادقة بالبصمة، المقاصد الآمنة (Intents)، الحافظة، الإشعارات، إدارة الملفات، ومعلومات الجهاز والبطارية — مقسمة بداخل حزم برمجية نظيفة تحت `com.mohamedzaitoon.android.core.*`.

🌐 **الموقع والتوثيق التفاعلي:** [apputilx.mohamedzaitoon.com](https://apputilx.mohamedzaitoon.com)  
📖 **English Version:** [README.md](README.md)

---

## 📦 التثبيت والتهيئة

### 1. إضافة التبعية للمشروع

```kotlin
// إضافة الإصدار في gradle/libs.versions.toml
[versions]
apputilx = "1.5.0-beta02"

[libraries]
apputilx = { group = "com.github.mohamed-zaitoon", name = "apputilx", version.ref = "apputilx" }

// إضافة المستودع في settings.gradle.kts
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

// إضافة التبعية في app/build.gradle.kts
dependencies {
    implementation(libs.apputilx)
}
```

### 2. تهيئة المكتبة بداخل كلاس التطبيق (Application)

```kotlin
import android.app.Application
import com.mohamedzaitoon.android.core.AppUtilX

class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
```

---

## 🚀 أمثلة الاستخدام المباشر لقطاعات المكتبة

```kotlin
import com.mohamedzaitoon.android.core.net.Network
import com.mohamedzaitoon.android.core.hardware.Audio
import com.mohamedzaitoon.android.core.hardware.Vibration
import com.mohamedzaitoon.android.core.content.Clipboard
import com.mohamedzaitoon.android.core.hardware.Device
import com.mohamedzaitoon.android.core.hardware.Battery

// فحص الاتصال بالإنترنت
val isOnline = Network.isConnected
val transport = Network.activeTransport()

// الصوتيات والاهتزاز
Audio.playClickSound()
Vibration.vibrate(200)

// النسخ للحافظة
Clipboard.copyText("مرحباً بك مع AppUtilX")

// معلومات الجهاز والبطارية
val device = Device.deviceName()
val battery = Battery.getBatteryLevel()
```

---

## 📄 حقوق النشر والترخيص

حقوق النشر © 2025–2027 محمد زيتون. جميع الحقوق محفوظة.
