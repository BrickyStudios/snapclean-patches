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
 * WILL NOT MATCH AS-IS: dexdump shows this string is defined in a `<clinit>` (a static
 *   constant), not a boolean gate, so the filter below finds no match and the patch fails
 *   to apply (hence EXPERIMENTAL, off by default). The real upsell-eligibility methods are
 *   obfuscated (names like `a`/`H`), so a plain string grep cannot find them.
 *
 * TARGET (hide-only, no subscription spoof): a boolean *eligibility* method that decides
 *   whether an upsell/paywall surface is shown; force it to return false. Locate it with a
 *   full jadx/REA decompile on a >4 GB box (OOMs here) and confirm on a device:
 *     "Decompile com.snapchat.android, find the boolean method that decides whether a
 *      Snapchat+ paywall / paid-lens upsell banner is shown (an *_UpsellEligibility check),
 *      and show its signature — NOT the SubscriptionInfo constructor."
 */
object StorePromoGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    filters = listOf(
        string("PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED"),
    ),
)
