package app.snapclean.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    /**
     * Snapchat on Android.
     *
     * Fingerprinted against the universal APK (arm64-v8a + armeabi-v7a, nodpi) from APKMirror,
     * which is a single [ApkFileType.APK]. 14.29.0.50 is confirmed present for the string/method
     * evidence in the fingerprints; later versions are allowed experimentally.
     */
    val COMPATIBILITY_SNAPCHAT = Compatibility(
        name = "Snapchat",
        packageName = "com.snapchat.android",
        apkFileType = ApkFileType.APK,
        appIconColor = 0xFFFC00, // Snapchat yellow.
        targets = listOf(
            // Confirmed analysed target (versionCode 322522).
            AppTarget(version = "14.29.0.50"),
            // Allow newer builds, flagged experimental until re-fingerprinted.
            AppTarget(version = null, isExperimental = true),
        ),
    )
}
