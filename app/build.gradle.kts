import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose.compiler)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.room)
  alias(libs.plugins.aboutlibraries)
}

android {
  namespace = "app.marlboroadvance.mpvex"
  compileSdk = 36

  defaultConfig {
    applicationId = "app.marlboroadvance.mpvex"
    minSdk = 26
    targetSdk = 36
    versionCode = 130
    versionName = "1.3.0"

    vectorDrawables {
      useSupportLibrary = true
    }

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    buildConfigField("String", "GIT_SHA", "\"${getCommitSha()}\"")
    buildConfigField("int", "GIT_COUNT", getCommitCount())
    buildConfigField("boolean", "ENABLE_UPDATE_FEATURE", "false")
    buildConfigField("boolean", "SCOPED_STORAGE_ONLY", "false")
  }

  signingConfigs {
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
      ndk {
        debugSymbolLevel = "none"
      }
    }

    debug {
      if (file("${rootDir}/debug.keystore").exists()) {
        signingConfig = signingConfigs.getByName("debugConfig")
      }
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-0"
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  buildFeatures {
    compose = true
    viewBinding = true
    buildConfig = true
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      excludes += "META-INF/DEPENDENCIES"
      excludes += "META-INF/LICENSE*"
      excludes += "META-INF/NOTICE*"
      excludes += "META-INF/*.kotlin_module"
      excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
    }
    jniLibs {
      useLegacyPackaging = true
    }
  }

  @Suppress("UnstableApiUsage")
  androidResources {
    generateLocaleConfig = true
  }
}

tasks.matching { it.name.contains("AarMetadata") || it.name.contains("stripDebugDebugSymbols") }.configureEach {
  enabled = false
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-Xcontext-parameters",
      "-Xannotation-default-target=param-property",
      "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi",
      "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
    )
    jvmTarget.set(JvmTarget.JVM_17)
  }
}

composeCompiler {
  includeSourceInformation = true
}

room {
  schemaDirectory("$projectDir/schemas")
}

dependencies {
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.material3.android)
  implementation(libs.material)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.ui.tooling.preview)
  debugImplementation(libs.androidx.ui.tooling)
  implementation(libs.bundles.compose.navigation3)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.compose.constraintlayout)
  implementation("androidx.preference:preference-ktx:1.2.1")
  implementation("androidx.constraintlayout:constraintlayout:2.2.0")
  implementation(libs.androidx.material3.icons.extended)
  implementation(libs.androidx.compose.animation.graphics)
  implementation(libs.mediasession)
  implementation(libs.androidx.documentfile)
  implementation(libs.saveable)

  implementation(platform(libs.koin.bom))
  implementation(libs.bundles.koin)

  implementation(libs.seeker)
  implementation(libs.compose.prefs)
  implementation(libs.aboutlibraries.compose.m3)

  implementation(libs.accompanist.permissions)

  implementation(libs.room.runtime)
  ksp(libs.room.compiler)
  implementation(libs.room.ktx)

  implementation(libs.kotlinx.immutable.collections)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.okhttp)

  implementation(libs.truetype.parser)
  implementation(libs.fsaf)
  implementation(libs.mediainfo.lib)
  implementation(libs.mpv.android)

  // Network protocol libraries
  implementation(libs.smbj)
  implementation(libs.commons.net)
  implementation(libs.sardine.android) {
    exclude(group = "xpp3", module = "xpp3")
  }
  implementation(libs.nanohttpd)
  implementation(libs.lazycolumnscrollbar)
  implementation(libs.reorderable)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}

/* ---------------- Git helpers ---------------- */

fun getCommitCount(): String =
  runCommand("git rev-list --count HEAD")?.toIntOrNull()?.toString() ?: "130"

fun getCommitSha(): String =
  runCommand("git rev-parse --short HEAD")?.takeIf { it.matches(Regex("[0-9a-fA-F]+")) } ?: "0000000"

fun runCommand(command: String): String? =
  try {
    val parts = command.split(' ')
    val process = ProcessBuilder(parts)
      .redirectErrorStream(false)
      .start()

    val output = process.inputStream
      .bufferedReader()
      .readText()
      .trim()

    val exitCode = process.waitFor()
    if (exitCode == 0 && output.isNotEmpty()) output else null
  } catch (e: Exception) {
    null
  }
