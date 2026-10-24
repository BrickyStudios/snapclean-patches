package app.snapclean.patches.location

import app.morphe.patcher.Fingerprint
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the method that delivers a fresh android.location.Location into the app
 * (e.g. an onLocationChanged / location-result callback).
 *
 * TODO: Validate against your target APK with REA before a stable release:
 *   "Decompile com.snapchat.android and find where a android.location.Location is
 *    received from the fused/location provider; show the callback method and which
 *    parameter is the Location."
 *   Add the confirmed `definingClass`, parameter order and any stable strings/filters,
 *   and update the injected register in SpoofLocationPatch if it is not p1.
 */
object LocationDeliveryFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "V",
    // Partial match: the callback takes a Location somewhere in its parameters.
    parameters = listOf("Landroid/location/Location;"),
)
