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

    // ضروري لمكتبات Android
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

afterEvaluate {

    val ghUser = findProperty("GITHUB_USERNAME") as String?
    val ghToken = findProperty("GITHUB_TOKEN") as String?

    publishing {

        publications {

            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "io.github.mohamed-zaitoon"
                artifactId = "apputilx"
                version = "1.0.3-beta"

                pom {
                    name.set("AppUtilx")
                    description.set("Android utility library with common helpers")
                    url.set("https://github.com/mohamed-zaitoon/apputilx")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("mohamed-zaitoon")
                            name.set("Mohamed Zaitoon")
                            email.set("mohamedzaitoon01@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/mohamed-zaitoon/apputilx.git")
                        developerConnection.set("scm:git:ssh://github.com/mohamed-zaitoon/apputilx.git")
                        url.set("https://github.com/mohamed-zaitoon/apputilx")
                    }
                }
            }
        }

        repositories {

            // 🔐 GitHub Packages (يشتغل فقط لو التوكن موجود)
            if (!ghUser.isNullOrBlank() && !ghToken.isNullOrBlank()) {
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/mohamed-zaitoon/apputilx")
                    credentials {
                        username = ghUser
                        password = ghToken
                    }
                }
            }

            // 🌍 JitPack (لا يحتاج أي إعدادات)
            maven {
                name = "JitPack"
                url = uri("https://jitpack.io")
            }
        }
    }
}