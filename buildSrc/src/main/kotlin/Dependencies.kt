object BuildConfig {
    const val compileSdk = 28
    const val minSdk = 16
    const val targetSdk = 28
    private const val versionMajor = 0
    private const val versionMinor = 0
    private const val versionPatch = 0
    private const val versionBuild = 1
    const val versionName =
        "$versionMajor.$versionMinor.$versionPatch"
    const val versionCode =
        versionMajor * 1000000 + versionMinor * 10000 + versionPatch * 100 + versionBuild

}

object CommonLibs {
    const val android_gradle_plugin = "com.android.tools.build:gradle:3.3.1"

    const val dexter = "com.karumi:dexter:5.0.0"
    const val phrase = "com.squareup.phrase:phrase:1.1.0"
    const val sonar = "com.facebook.sonar:sonar:0.0.1"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val junit = "junit:junit:4.12"
    const val truth = "com.google.truth:truth:0.42"
    const val javaxInject = "javax.inject:javax.inject:1"
    const val rabbkt = "com.aungkyawpaing.rabbkt:rabbkt:1.0.1"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:2.2"
    const val easy_image = "com.github.jkwiecien:EasyImage:1.3.1"
    const val android_crop = "com.soundcloud.android:android-crop:1.0.1@aar"
    const val ucrop = "com.github.yalantis:ucrop:2.2.2"
    const val circle_image_view = "de.hdodenhof:circleimageview:3.0.0"
    const val flex_box = "com.google.android:flexbox:2.0.1"
    const val expandable_rv = "com.thoughtbot:expandablerecyclerview:1.3"
    const val dotIndicator = "me.relex:circleindicator:2.1.0"
    const val dotPageIndicator = "com.romandanylyk:pageindicatorview:1.0.3"
    const val imageEditor = "com.theartofdev.edmodo:android-image-cropper:2.8.+"
    const val xmlToJson = "com.github.smart-fun:XmlToJson:1.1.1"

}

object Worker {
    private const val work_version = "2.0.0"
    const val worker = "android.arch.work:work-runtime:$work_version"
    const val ktx = "androidx.work:work-runtime-ktx:$work_version"
    const val rxjava2Support = "androidx.work:work-rxjava2:$work_version"

}

//region AndroidX
object AndroidXAnnotations {
    const val annotations = "androidx.annotation:annotation:1.0.1"
    const val jobIntentService = "com.android.support:support-compat:26.0.0-beta2"
}

object AndroidXAppCompat {
    const val app_compat = "androidx.appcompat:appcompat:1.1.0"
}

object AndroidXRecyclerView {
    private const val version = "1.0.0"
    const val recycler_view = "androidx.recyclerview:recyclerview:$version"
    const val selection = "androidx.recyclerview:recyclerview-selection:$version"
}

object AndroidXCardView {
    const val card_view = "androidx.cardview:cardview:1.0.0"
}

object AndroidXConstraintLayout {
    private const val version = "2.0.0-alpha2"
    const val constraint_layout = "androidx.constraintlayout:constraintlayout:$version"
}

object AndroidXViewPager {
    const val view_pager = "androidx.viewpager:viewpager:1.0.0"
}

object AndroidXLegacy {
    private const val version = "1.0.0"
    const val support_v4 = "androidx.legacy:legacy-support-v4:$version"
}

object AndroidXPalette {
    const val palette = "androidx.palette:palette:1.0.0"
}

object AndroidXSqlite {
    private const val version = "2.0.0-rc01"
    const val sqlite = "androidx.sqlite:sqlite:$version"
    const val sqlite_framework = "androidx.sqlite:sqlite-framework:$version"
    const val sqlite_ktx = "androidx.sqlite:sqlite-ktx:$version"
}

object AndroidArchLifeCycle {
    private const val version = "2.0.0"
    const val lifecycle = "androidx.lifecycle:lifecycle-extensions:$version"
    const val lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:$version"
    const val lives = "com.snakydesign.livedataextensions:lives:1.2.1"
}

object AndroidArchCore {
    private const val version = "1.1.1"
    const val test = "android.arch.core:core-testing:$version"
}

object AndroidArchWork {
    private const val version = "1.0.0-alpha12"
    const val work_runtime = "android.arch.work:work-runtime:$version"
    const val work_runtime_ktx = "android.arch.work:work-runtime-ktx:$version"
}

object AndroidArchNavigation {
    private const val version = "1.0.0-alpha08"
    const val common = "android.arch.navigation:navigation-common:$version"
    const val common_ktx = "android.arch.navigation:navigation-common-ktx:$version"
    const val fragment = "android.arch.navigation:navigation-fragment:$version"
    const val fragment_ktx = "android.arch.navigation:navigation-fragment-ktx:$version"
    const val runtime = "android.arch.navigation:navigation-runtime:$version"
    const val runtime_ktx = "android.arch.navigation:navigation-runtime-ktx:$version"
    const val safe_args_generator =
        "android.arch.navigation:navigation-safe-args-generator:$version"
    const val safe_args_plugin =
        "android.arch.navigation:navigation-safe-args-gradle-plugin:$version"
    const val testing = "android.arch.navigation:navigation-testing:$version"
    const val testing_ktx = "android.arch.navigation:navigation-testing-ktx:$version"
    const val ui = "android.arch.navigation:navigation-ui:$version"
    const val ui_ktx = "android.arch.navigation:navigation-ui-ktx:$version"
}

object AndroidXCore {
    private const val version = "1.2.0"
    const val core = "androidx.core:core:$version"
    const val core_ktx = "androidx.core:core-ktx:$version"
}

object AndroidXFragment {
    private const val version = "1.1.0-alpha03"

    const val fragment = "androidx.fragment:fragment:$version"
    const val fragment_ktx = "androidx.fragment:fragment-ktx:$version"
    const val fragment_testing = "androidx.fragment:fragment-testing:$version"
}

object AndroidXTest {
    private const val version = "1.1.0"
    const val core = "androidx.test:core:$version"
    const val core_ktx = "androidx.test:core-ktx:1.1.0-beta01"
    const val runner = "androidx.test:runner:$version"
    const val rules = "androidx.test:rules:$version"
    const val roboelectric = "org.robolectric:robolectric:4.2"
}

object AndroidXTestExt {
    private const val version = "1.1.0"
    const val junit = "androidx.test.ext:junit:$version"
    const val junit_ktx = "androidx.test.ext:junit-ktx:$version"
    const val truth = "androidx.test.ext:truth:$version"
}

object AndroidXEspresso {
    private const val version = "3.1.0"
    const val core = "androidx.test.espresso:espresso-core:$version"
    const val contrib = "androidx.test.espresso:espresso-contrib:$version"
    const val intents = "androidx.test.espresso:espresso-intents:$version"
    const val idling_resource = "androidx.test.espresso:espresso-idling-resource:$version"
    const val idling_concurrent = "androidx.test.espresso.idling:idling-concurrent:$version"
    const val rx_idler = "com.squareup.rx.idler:rx2-idler:0.9.0"
}

object AndroidXLocalBroadcastManager {
    const val local_broadcast_manager = "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0"
}

//endregion

object Material {
    private const val oldVersion = "1.1.0-alpha04"
    private const val version = "1.2.0-alpha05"
    const val material = "com.google.android.material:material:$version"
}

object Dagger {
    private const val version = "2.21"
    const val core = "com.google.dagger:dagger:$version"
    const val compiler = "com.google.dagger:dagger-compiler:$version"
    const val android_core = "com.google.dagger:dagger-android:$version"
    const val android_support = "com.google.dagger:dagger-android-support:$version"
    const val android_processor = "com.google.dagger:dagger-android-processor:$version"
}

object EverNoteJob {
    private const val version = "1.2.6"

    const val core = "com.evernote:android-job:$version"
}

object Facebook {
    const val facebook_login = "com.facebook.android:facebook-login:[5,6)"
    const val account_kit = "com.facebook.android:account-kit-sdk:4.39.0"
}

object Firebase {
    private const val version = "16.0.6"

    const val auth = "com.google.firebase:firebase-auth:$version"
    const val core = "com.google.firebase:firebase-core:$version"
    const val db = "com.google.firebase:firebase-database:$version"
    const val messaging = "com.google.firebase:firebase-messaging:17.3.4"
    const val analytics = "com.google.firebase:firebase-analytics:17.2.2"
    const val crashlytics = "com.google.firebase:firebase-crashlytics:17.0.0-beta01"
    const val fabric_plugin = "io.fabric.tools:gradle:1.26.1"
}

object FragmentTestRule {
    private const val version = "1.1.0"
    const val android = "com.21buttons:fragment-test-rule:$version"
    const val extra = "com.21buttons:fragment-test-rule-extras:$version"
}

object Glide {
    private const val version = "4.9.0"
    const val core = "com.github.bumptech.glide:glide:$version"
    const val compiler = "com.github.bumptech.glide:compiler:$version"
    const val transformations = "jp.wasabeef:glide-transformations:4.0.1"
}

object Coroutines {
    private const val coroutineAdapterVersion = "0.9.2"
    private const val coroutineVersion = "1.3.2"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion"
    const val coroutinesAdapter =
        "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:$coroutineAdapterVersion"
    const val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion"

}

object GoogleService {
    const val auth = "com.google.android.gms:play-services-auth:16.0.1"
    const val ads = "com.google.android.gms:play-services-ads:17.1.2"
    const val consent = "com.google.android.ads.consent:consent-library:1.0.6"
    const val gms = "com.google.gms:google-services:4.2.0"
}

object Kotlin {
    private const val version = "1.3.20"

    const val stdblib_jdk = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"

    const val gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
}

object Room {
    private const val version = "2.2.1"
    const val roomRuntime = "androidx.room:room-runtime:$version"
    const val compiler = "androidx.room:room-compiler:$version"
    const val ktxCoroutineSupport = "androidx.room:room-ktx:$version"
}

object Lottie {
    private const val version = "3.0.7"
    const val lottie = "com.airbnb.android:lottie:$version"
}

object Mockito {
    private const val version = "2.23.4"

    const val core = "org.mockito:mockito-core:$version"
    const val android = "org.mockito:mockito-android:$version"
    const val inline = "org.mockito:mockito-inline:$version"
    const val kotlin = "com.nhaarman:mockito-kotlin:1.5.0"
}

object LeakCanary {
    private const val version = "1.6.1"

    const val core = "com.squareup.leakcanary:leakcanary-android:$version"
    const val no_op = "com.squareup.leakcanary:leakcanary-android-no-op:$version"
    const val support_fragment = "com.squareup.leakcanary:leakcanary-support-fragment:$version"
}

object Moshi {
    private const val version = "1.6.0"

    const val core = "com.squareup.moshi:moshi:$version"
    const val code_gen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
}

object OkHttp {
    private const val version = "3.11.0"

    const val client = "com.squareup.okhttp3:okhttp:$version"
    const val logger = "com.squareup.okhttp3:logging-interceptor:$version"
}

object ReactiveX {
    const val rxjava = "io.reactivex.rxjava2:rxjava:2.2.6"
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val rxKotlin = "io.reactivex.rxjava2:rxkotlin:2.3.0"
    const val rxComprehension = "com.github.pakoito.RxComprehensions:rxcomprehensions2:1.3.0"
}

object RxBinding {
    private const val version = "3.0.0-alpha2"

    const val platform = "com.jakewharton.rxbinding3:rxbinding:$version"
    const val appcompat = "com.jakewharton.rxbinding3:rxbinding-appcompat:$version"
}

object Chunk {
    private const val version = "1.1.0"
    const val debug = "com.readystatesoftware.chuck:library:$version"
    const val release = "com.readystatesoftware.chuck:library-no-op:$version"
}

object Retrofit {
    private const val version = "2.7.1"

    const val retrofit = "com.squareup.retrofit2:retrofit:$version"
    const val moshi_converter = "com.squareup.retrofit2:converter-moshi:$version"
    const val gson = "com.google.code.gson:gson:2.8.5"
    const val gsonConverter = "com.squareup.retrofit2:converter-gson:$version"
    const val rxJavaAdapter = "com.squareup.retrofit2:adapter-rxjava2:2.4.0"
    const val scalarsConverter = "com.squareup.retrofit2:converter-scalars:2.4.0"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:4.4.0"
    const val okhttp = "com.squareup.okhttp3:okhttp:4.4.0"
    const val xmlConverter = "com.squareup.retrofit2:converter-simplexml:2.5.0"
    const val worker = "android.arch.work:work-runtime:1.0.0"


    private const val dagger_assist_version = "0.5.2"
    const val assistedDaggerAnnotation =
        "com.squareup.inject:assisted-inject-annotations-dagger2:$dagger_assist_version"
    const val assistedDaggerAnnotationProcessor =
        "com.squareup.inject:assisted-inject-processor-dagger2:$dagger_assist_version"
}

object SqlDelight {
    private const val version = "1.0.3"

    const val gradle_plugin = "com.squareup.sqldelight:gradle-plugin:$version"
    const val android_driver = "com.squareup.sqldelight:android-driver:$version"
    const val runtime = "com.squareup.sqldelight:runtime-common::$version"
}

object Stetho {
    private const val version = "1.5.0"

    const val core = "com.facebook.stetho:stetho:$version"
    const val okhttp3 = "com.facebook.stetho:stetho-okhttp3:$version"
}

object CommonCodec {
    const val codec = "commons-codec:commons-codec:1.10"
    const val commonio = "commons-io:commons-io:2.5"
}

object ExoPlayer {
    private const val version = "2.11.1"

    const val exoplayer = "com.google.android.exoplayer:exoplayer:$version"
    const val core = "com.google.android.exoplayer:exoplayer-core:$version"
    const val dash = "com.google.android.exoplayer:exoplayer-core:$version"
    const val ui = "com.google.android.exoplayer:exoplayer-core:$version"

}

object WorkManager {
    private const val work_version = "2.4.0-alpha02"

    // (Java only)
    const val runtime = "androidx.work:work-runtime:$work_version"

    // Kotlin + coroutines
    const val ktx = "androidx.work:work-runtime-ktx:$work_version"

    // optional - RxJava2 support
    const val rxSupport = "androidx.work:work-rxjava2:$work_version"

    // optional - GCMNetworkManager support
    const val gcmNetworkManager = "androidx.work:work-gcm:$work_version"

    // optional - Test helpers
    const val testing = "androidx.work:work-testing:$work_version"
}

object Testing {

}

object VdoPlayer {
    private const val version = "1.3.3"
    const val vdoPlayer = "com.vdocipher.aegis:vdocipher-android:$version"
}

object Shimmer {
    const val recycler_view = "com.github.sharish:ShimmerRecyclerView:v1.3"
}


object AndroidDebugDatabase {
    private const val version = "1.0.6"
    const val core = "com.amitshekhar.android:debug-db:$version"
    const val encrypted = "com.amitshekhar.android:debug-db-encrypt:$version"
}

object ThreeTenBp {
    private const val version = "1.3.8"

    const val core = "org.threeten:threetenbp:$version"
    const val no_tz_db = "org.threeten:threetenbp:$version:no-tzdb"
    const val android = "com.jakewharton.threetenabp:threetenabp:1.1.2"
}


object SocketIO {
    const val core = "io.socket:socket.io-client:1.0.0"
}

object ZXing {
    const val core = "com.google.zxing:core:3.3.0"
    const val parent = "com.google.zxing:zxing-parent:3.4.0"
    const val embedded = "com.journeyapps:zxing-android-embedded:4.1.0"
}

object OSMDroid{
    private const val version = "6.1.6"
    const val core = "org.osmdroid:osmdroid-android:$version"
}

object Lost{
    const val core = "com.mapzen.android:lost:3.0.2"
}