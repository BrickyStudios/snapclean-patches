package app.snapclean.patches.ads

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the method that decides whether an ad slot should be served/rendered.
 *
 * Snapchat's class and method names are obfuscated and change every release, so this
 * fingerprint is matched by stable *string* hints and shape, not by exact names.
 *
 * TODO: Validate and tighten against your target APK before a stable release.
 *   Run REA to locate the real method and its surrounding strings/opcodes:
 *     "Decompile com.snapchat.android and find where ad slots are gated; show the
 *      method that returns whether an ad is served, with the evidence."
 *   Then replace the string hints below with the ones REA reports, add `returnType`,
 *   `parameters` and `filters`, and confirm the method returns a boolean.
 *
 * See https://github.com/MorpheApp/morphe-patcher/blob/main/docs/2_2_1_fingerprinting.md
 */
object AdServingGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    filters = listOf(
        // TODO: replace with a confirmed stable string near the ad gate (verify with REA).
        string("ad_request"),
    ),
)
