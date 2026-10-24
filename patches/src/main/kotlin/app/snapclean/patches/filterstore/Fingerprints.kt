package app.snapclean.patches.filterstore

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the gate that decides whether a paid-lens / Snapchat+ promo surface is shown.
 *
 * TODO: Validate against your target APK with REA before a stable release:
 *   "Decompile com.snapchat.android and find where the Snapchat+ upsell and paid-lens
 *    promos are gated; show the boolean method and the nearby strings."
 *   Replace the string hint below with a confirmed stable one and tighten the shape.
 */
object StorePromoGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    filters = listOf(
        // TODO: confirm a stable string near the upsell gate with REA (e.g. a subscription SKU key).
        string("snapchat_plus"),
    ),
)
