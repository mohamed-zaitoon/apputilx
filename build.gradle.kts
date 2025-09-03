plugins {
    id("com.android.application") version "8.10.0" apply false
    id("com.android.library") version "8.10.0" apply false
    kotlin("android") version "2.2.10" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.10.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
    }
}
