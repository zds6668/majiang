// app/build.gradle.kts

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt") // 启用 kapt 插件，用于 Room 数据库的注解处理
}

android {
    compileSdk = 33 // 使用适当的 compileSdkVersion

    // 这里设置项目的命名空间，替换为您自己的命名空间
    namespace = "com.example.majiang"  // 请根据项目实际的包名修改

    defaultConfig {
        applicationId = "com.example.majiang"  // 应用的包名
        minSdk = 21  // 设置最低 SDK 版本
        targetSdk = 33 // 设置目标 SDK 版本
        versionCode = 1 // 版本号
        versionName = "1.0" // 版本名称
    }
    compileOptions {
        // 设置 Java 编译器的兼容性版本
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"  // 确保 Kotlin 目标与 Java 版本匹配
    }



    buildTypes {
        getByName("release") {
            isMinifyEnabled = false // 是否启用混淆
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.android.material:material:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0") // Kotlin 标准库
    // Room 数据库相关依赖
    implementation("androidx.room:room-runtime:2.5.0") // Room 库
    kapt("androidx.room:room-compiler:2.5.0") // Room 编译时注解处理

    // Room 扩展库（如需要使用 LiveData 或其他功能）
    implementation("androidx.room:room-ktx:2.5.0")

    // Lifecycle 组件（如果你需要使用 LiveData、ViewModel 等功能）
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // ConstraintLayout
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // AppCompat 库
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Fragment 支持库（如果你使用 Fragment）
    implementation("androidx.fragment:fragment-ktx:1.5.5")

    // 如果需要使用 Navigation 组件
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")

}
