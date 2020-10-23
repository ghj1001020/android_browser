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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Annotation은 자바스크립트 때문에 난독화하지 않음
-keepattributes *Annotation*    # 앞뒤로 Annotation 이름을 갖는 속성 유지
-keepattributes Signature       # Generic Type 속성 유지
-keepattributes InnerClasses    # 내부 클래스 속성 유지

-adaptresourcefilenames **.properties,**.xml,**.png,**.jpg,**.gif


# 네이티브 메소드를 포함하고 있는 클래스 및 멤버이름 유지
-keepclasseswithmembernames class * {
    native <methods>;
}


# 다음 public static 으로 된 enum 유지
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


# Parcelabe 구현 필드 유지
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}


# 다음 직렬화를 구현한 모든 클래스의 멤버에 대한 난독화 제거
-keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        static final java.io.ObjectStreamField[] serialPersistentFields;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
}


# R class
-keep class **.R
-keep class **.R$* {
    <fields>;
}


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------


##---------------Begin: proguard configuration for Javascript  ----------
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keep class com.ghj.browser.webkit.JsBridge

##---------------End: proguard configuration for Javascript  ----------

