# AppUtilX Flutter Integration Example

This example demonstrates how to integrate the **AppUtilX** Android library (v1.5.0-beta02) into a Flutter application using a native Kotlin `MethodChannel` bridge.

---

## 🚀 Setup Instructions

### 1. Android Application Class (`MyApp.kt`)
In `android/app/src/main/kotlin/com/mohamedzaitoon/android/apps/exampleflutter/MyApp.kt`:

```kotlin
package com.mohamedzaitoon.apputilx.apps.exampleflutter

import android.app.Application
import com.mohamedzaitoon.apputilx.AppUtilX

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppUtilX.initialize(this)
    }
}
```

### 2. MethodChannel Handler (`MainActivity.kt`)
In `android/app/src/main/kotlin/com/mohamedzaitoon/android/apps/exampleflutter/MainActivity.kt`:

```kotlin
package com.mohamedzaitoon.apputilx.apps.exampleflutter

import com.mohamedzaitoon.apputilx.AppUtilX
import com.mohamedzaitoon.apputilx.net.Network
import com.mohamedzaitoon.apputilx.content.Clipboard
import com.mohamedzaitoon.apputilx.hardware.Vibration
import com.mohamedzaitoon.apputilx.hardware.Audio
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterFragmentActivity() {
    private val channel = "apputilx/core"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler { call, result ->
            when (call.method) {
                "isConnected" -> result.success(Network.isConnected)
                "vibrate" -> {
                    Vibration.vibrate(call.argument<Int>("ms")?.toLong() ?: 200)
                    result.success(null)
                }
                "copyText" -> {
                    Clipboard.copyText(call.argument<String>("text") ?: "")
                    result.success(null)
                }
                "audioInfo" -> {
                    Audio.playClickSound()
                    result.success("Muted=${Audio.isMuted()} MusicVolume=${Audio.getMusicVolume()}%")
                }
                else -> result.notImplemented()
            }
        }
    }
}
```

### 3. Flutter Dart Invocation
In `lib/main.dart`:

```dart
import 'package:flutter/services.dart';

const _channel = MethodChannel('apputilx/core');

// Check connectivity
final isOnline = await _channel.invokeMethod<bool>('isConnected');

// Copy text & play tactile sound
await _channel.invokeMethod('copyText', {'text': 'Hello AppUtilX'});
await _channel.invokeMethod('vibrate', {'ms': 200});
```

---

## 📄 License
Copyright (c) 2025–2027 Mohamed Zaitoon. All rights reserved.
