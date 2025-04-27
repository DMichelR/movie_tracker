plugins {
    id("com.android.application") version "8.9.2" apply false
    id("com.android.library") version "8.9.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.16" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.9" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}