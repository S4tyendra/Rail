// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
buildscript {
     val objectboxVersion by extra("4.0.2") // For KTS build scripts

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(libs.gradle)
        classpath("io.objectbox:objectbox-gradle-plugin:$objectboxVersion")
    }
}
