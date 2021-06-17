# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
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
#-keep public class * implements com.bumptech.glide.module.GlideModule
#-keep public class * extends com.bumptech.glide.module.AppGlideModule
#-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
#  **[] $VALUES;
#  public *;
#}
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
-keep class androidx.appcompat.widget.** { *; }
#-keep class android.view.** { *; }
#-keep class android.graphics.drawable.** { *; }
#-keep class android.widget.** { *; }
#-keep class android.** { *; }
#-keep class androidx.** { *; }
-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.my.package.beans.** { *; }
-keep class com.example.abumuhsin.udusmini_library.firebaseStuff.model.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-keepnames class ir.beigirad.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn ir.beigirad.**
-dontwarn com.shockwave.**
-keep class com.shockwave.**
#-keep class com.tenclouds.** { *; }
#-keep class de.hdodenhof.circleimageview.** { *; }
# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
