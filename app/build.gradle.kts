plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.wizsmith.lockmanager'
    compileSdk 34

    defaultConfig {
        applicationId "com.wizsmith.lockmanager"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Retrofit
    implementation "com.squareup.retrofit2:retrofit:2.11.0"
    implementation "com.squareup.retrofit2:converter-gson:2.11.0"

    // Coroutines
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"

    // Glide
    implementation "com.github.bumptech.glide:glide:4.16.0"

    // Material Design + Drawer
    implementation 'com.google.android.material:material:1.11.0'

    // RecyclerView
    implementation "androidx.recyclerview:recyclerview:1.3.2"
}
