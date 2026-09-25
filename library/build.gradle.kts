import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

plugins {
    id("com.android.library")
    id("maven-publish")
}

configure<LibraryExtension> {
    namespace = "apputilx"

    compileSdk = 35
    buildToolsVersion = "37.0.0"
    ndkVersion = "28.2.13676358"

    defaultConfig {
        minSdk = 28
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.mohamed-zaitoon"
                artifactId = "apputilx"
                version = "1.5.0-alpha03"
                from(components["release"])
            }
        }
    }
}

dependencies {
    api("androidx.core:core-ktx:1.15.0")
    api("androidx.appcompat:appcompat:1.8.0")
    api("com.google.android.material:material:1.12.0")
    api("androidx.browser:browser:1.8.0")
    api("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    api("androidx.biometric:biometric:1.2.0-alpha05")
    api("androidx.lifecycle:lifecycle-process:2.8.7")
}
