package app.snapclean.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    /**
     * Snapchat on Android.
     *
     * Snapchat is distributed on APKMirror as a split bundle, so [apkFileType] is [ApkFileType.APKM]
     * and Morphe Manager will redirect the user to APKMirror.
     *
     * shortcut: targets only "any version, experimental" until a confirmed-working version is pinned.
     *   Pin the exact version you fingerprinted against (add `AppTarget(version = "<x.y.z.w>")`)
     *   before cutting a stable release, so users have a 100%-tested target. See README "Updating".
     */
    val COMPATIBILITY_SNAPCHAT = Compatibility(
        name = "Snapchat",
        packageName = "com.snapchat.android",
        apkFileType = ApkFileType.APKM,
        appIconColor = 0xFFFC00, // Snapchat yellow.
        targets = listOf(
            AppTarget(
                version = null,
                isExperimental = true,
            ),
        ),
    )
}
