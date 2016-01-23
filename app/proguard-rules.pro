# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Programmation_Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and action by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# Crashlytics
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile,LineNumberTable

# Keep the class.
-keep class com.mercandalli.android.android.apps.files.** { *; }

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class retrofit.** { *; }
-dontwarn retrofit.**

-dontwarn okio.**

-dontwarn org.apache.regexp.**

-keep class com.google.gson.** { *; }
-keep class com.google.inject.* { *; }
-keep class org.apache.http.* { *; }
-keep class org.apache.james.mime4j.* { *; }
-keep class javax.inject.* { *; }
-keep class retrofit.* { *; }
-dontwarn rx.*
-keep class com.example.testobfuscation.** { *; }
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

-dontwarn org.jaudiotagger.**
-keep class org.jaudiotagger.** { *; }
