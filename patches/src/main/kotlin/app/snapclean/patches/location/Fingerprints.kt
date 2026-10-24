package app.snapclean.patches.location

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the standard LocationListener callback that delivers a fresh
 * android.location.Location into the app.
 *
 * EVIDENCE (Snapchat 14.29.0.50, versionCode 322522): dexdump confirms real methods
 *   `onLocationChanged(Landroid/location/Location;)V` in classes2.dex and classes3.dex,
 *   plus a reference to com.google.android.gms.location.internal.IFusedLocationProviderCallback.
 * This signature is real, so SpoofLocationPatch injects at p1 (the Location) with confidence.
 *
 * Note: more than one method matches; overriding every delivered Location is the intended
 * behaviour for a spoof. If you want to narrow it, add the confirmed definingClass via REA.
 */
object LocationDeliveryFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "V",
    parameters = listOf("Landroid/location/Location;"),
)
