import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

configure<LibraryExtension> {
    namespace = "apputilx"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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

    //noinspection WrongGradleMethod
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
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.browser)
    api(libs.androidx.swiperefreshlayout)
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
                version = "1.2.0"

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
