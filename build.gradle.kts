// build.gradle.kts

buildscript {
    repositories {
        google()      // 插件仓库：Google
        mavenCentral() // 插件仓库：Maven Central
    }

    val kotlin_version = "1.8.0"

    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()      // 项目依赖仓库：Google
        mavenCentral() // 项目依赖仓库：Maven Central
    }
}
