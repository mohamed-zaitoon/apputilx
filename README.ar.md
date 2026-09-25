[![](https://jitpack.io/v/mohamed-zaitoon/apputilx.svg)](https://jitpack.io/#mohamed-zaitoon/apputilx)
![AndroidX](https://img.shields.io/badge/AndroidX-Required-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-First-purple)
![Platform](https://img.shields.io/badge/Platform-Android-green)
![Release](https://img.shields.io/badge/Release-orange)

# AppUtilX (باللغة العربية)

> مكتبة أدوات وأدوات مساعدة خفيفة وعالية الأداء لتطبيقات أندرويد و فلاتر.

تجمع مكتبة **AppUtilX** جميع المهام المكررة في تطوير أندرويد — مثل فحص الشبكة، الاهتزاز الفعلي، الصوتيات والشاشة، المصادقة بالبصمة، المقاصد الآمنة (Intents)، الحافظة، الإشعارات، إدارة الملفات، ومعلومات الجهاز والبطارية — بداخل واجهة برمجية ناصعة وبسيطة.

🌐 **الموقع والتوثيق التفاعلي:** [apputilx.mohamedzaitoon.com](https://apputilx.mohamedzaitoon.com)  
📖 **English Version:** [README.md](README.md)

---

## 📦 التثبيت والتهيئة

### 1. إضافة التبعية للمشروع

```kotlin
// إضافة الإصدار في gradle/libs.versions.toml
[versions]
apputilx = "1.5.0-beta01"

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

#### كوتلن (Kotlin Native)
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

#### فلاتر (Flutter Android Host `MyApp.kt`)
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

## 📄 حقوق النشر والترخيص

حقوق النشر © 2025–2027 محمد زيتون. جميع الحقوق محفوظة.
