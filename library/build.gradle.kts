plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "apputilx"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    buildFeatures {
        viewBinding = true
    }

    // مهم جدًا مع Android + maven-publish
    publishing {
        singleVariant("release") {
            withSourcesJar()

        }
    }
}

dependencies {
    api("androidx.core:core-ktx:1.17.0")
    api("androidx.appcompat:appcompat:1.7.1")
    api("com.google.android.material:material:1.13.0")
    api("androidx.browser:browser:1.9.0")
    api("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0")
}

/* ---------- GitHub Packages Publishing ---------- */

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.github.mohamed-zaitoon"
                artifactId = "apputilx"
                version = "1.0.0-c"
            }
        }

        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/mohamed-zaitoon/apputilx")

                credentials {
                    username = project.findProperty("gpr.user") as String?
                    password = project.findProperty("gpr.key") as String?
                }
            }
        }
    }
}