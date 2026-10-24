package app.snapclean.patches.filterstore

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the gate that decides whether a paid-lens / Snapchat+ promo surface is shown.
 *
 * EVIDENCE (Snapchat 14.29.0.50): the flag `PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED` is present
 *   in classes.dex (confirmed via dexdump), alongside `SUBSCRIPTION_TIER_SNAPCHAT_PLUS_LENS`
 *   and many `*_UPSELL_*` keys.
 *
 * NOT YET METHOD-VERIFIED: confirm this string sits in a boolean gate with a bytecode pass
 *   (full jadx/REA decompile — needs a >4 GB box) before relying on this patch:
 *     "Decompile com.snapchat.android, find the method referencing
 *      PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED, and show whether it gates the paywall/upsell."
 */
object StorePromoGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    filters = listOf(
        string("PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED"),
    ),
)
