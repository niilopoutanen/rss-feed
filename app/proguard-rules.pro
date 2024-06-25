# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class com.niilopoutanen.rss_feed.database.AppRepository { *; }
-keep class com.niilopoutanen.rss_feed.database.AppViewModel { *; }
-keep class com.niilopoutanen.rss_feed.database.DatabaseThread { *; }
-keep class com.niilopoutanen.rss_feed.database.compatibility.SourceMigration { *; }
-keep class com.niilopoutanen.rss_feed.database.dao.SourceDao { *; }
-keep class com.niilopoutanen.rss_feed.manager.ImportActivity { *; }

-dontwarn com.niilopoutanen.rss_feed.database.AppRepository
-dontwarn com.niilopoutanen.rss_feed.database.AppViewModel
-dontwarn com.niilopoutanen.rss_feed.database.DatabaseThread
-dontwarn com.niilopoutanen.rss_feed.database.compatibility.SourceMigration
-dontwarn com.niilopoutanen.rss_feed.database.dao.SourceDao
-dontwarn com.niilopoutanen.rss_feed.manager.ImportActivity