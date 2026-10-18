-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class com.corechiper.assistant.data.local.** { *; }
-dontwarn com.google.mlkit.**
