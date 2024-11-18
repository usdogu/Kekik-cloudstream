import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import com.android.build.gradle.BaseExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        // Shitpack repo which contains our tools and dependencies
        maven("https://jitpack.io")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.0.4")
        // Cloudstream gradle plugin which makes everything work and builds plugins
        classpath("com.github.recloudstream:gradle:68db7215ec")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.cloudstream(configuration: CloudstreamExtension.() -> Unit) = extensions.getByName<CloudstreamExtension>("cloudstream").configuration()

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    apply(plugin = "com.lagradost.cloudstream3.gradle")

    cloudstream {
        // when running through github workflow, GITHUB_REPOSITORY should contain current repository name
        setRepo(System.getenv("GITHUB_REPOSITORY") ?: "https://github.com/keyiflerolsun/Kekik-cloudstream")

        authors = listOf("keyiflerolsun")
    }

    android {
        defaultConfig {
            minSdk = 21
            compileSdkVersion(33)
            targetSdk = 33
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8" // Required
                // Disables some unnecessary features
                freeCompilerArgs = freeCompilerArgs +
                        "-Xno-call-assertions" +
                        "-Xno-param-assertions" +
                        "-Xno-receiver-assertions"
            }
        }
    }

    dependencies {
        val apk by configurations
        val implementation by configurations

        // Stubs for all Cloudstream classes
        apk("com.lagradost:cloudstream3:pre-release")

        // these dependencies can include any of those which are added by the app,
        // but you dont need to include any of them if you dont need them
        // https://github.com/recloudstream/cloudstream/blob/master/app/build.gradle
        implementation(kotlin("stdlib"))                                              // Kotlin'in temel kütüphanesi
        implementation("com.github.Blatzar:NiceHttp:0.4.11")                          // HTTP kütüphanesi
        implementation("org.jsoup:jsoup:1.18.1")                                      // HTML ayrıştırıcı
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.0")   // Kotlin için Jackson JSON kütüphanesi
        implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")          // JSON-nesne dönüştürme kütüphanesi
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
