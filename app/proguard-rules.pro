# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Room database classes
-keep class com.expirytracker.data.database.** { *; }

# Keep data model classes
-keep class com.expirytracker.data.** { *; }

# Keep ViewModel classes
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
