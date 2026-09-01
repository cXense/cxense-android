-keep class io.piano.android.cxense.model.* { <fields>; }

-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault
-keepattributes *Annotation*

-adaptresourcefilecontents **.kotlin_module

-keepclassmembers class **$WhenMappings {
    <fields>;
}
-dontwarn kotlinx.coroutines.**

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    public static void checkNotNullParameter(java.lang.Object, java.lang.String);
}
