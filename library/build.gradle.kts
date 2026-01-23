import com.android.build.api.dsl.LibraryExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.android.library)
    id("maven-publish")
}

configure<LibraryExtension> {
    namespace = "apputilx"

    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
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

    buildFeatures {
        viewBinding = true
    }

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
    
                version = libs.versions.versionName.get()

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
                            email.set("mohamedzaitoon242@gmail.com")
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
        }
    }
}
