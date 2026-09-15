# Applied when the androidTest APK itself is minified (testProguardFiles), which only
# happens because testBuildType points at a minified build type.
#
# With core library desugaring on, the test APK carries its own L8-shrunk copy of the
# desugared library and it shadows the app's. L8 shrinks it to what the *test* code
# uses, so WorkDatabase's Collections.synchronizedMap call lands on a
# j$.util.DesugarCollections without that method and the process dies during
# ContentProvider install, before any test runs.
-keep class j$.** { *; }
-dontwarn j$.**
