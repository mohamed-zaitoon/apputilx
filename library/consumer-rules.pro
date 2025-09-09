# منع تشفير جميع الكلاسات داخل الباكدج hrm.widget
-keep class hrm.widget.** { *; }

# منع تشفير جميع الكلاسات داخل الباكدج hrm.utils
-keep class hrm.utils.** { *; }

# منع تشفير جميع الكلاسات داخل الباكدج hrm.core
-keep class hrm.core.** { *; }

# منع تشفير أسماء الإنر كلاس (inner classes)
-keepattributes InnerClasses

# منع تشفير التعليقات التوضيحية (annotations)
-keepattributes *Annotation*