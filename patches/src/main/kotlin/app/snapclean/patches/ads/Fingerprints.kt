package app.snapclean.patches.ads

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the method that decides whether an ad slot should be served/rendered.
 *
 * Snapchat's class and method names are obfuscated, so this fingerprint matches by a
 * stable *string* that provably exists in the target, plus the boolean-gate shape.
 *
 * EVIDENCE (Snapchat 14.29.0.50): the string `skip_ad_insertion_with_null_group_id` is
 *   present in classes3.dex (confirmed via dexdump), alongside ad-pipeline symbols such as
 *   `RemoveAdServeItemDurableJobProcessor` and `SerializedSponsoredInfo(isSponsored=`.
 *
 * NOT YET METHOD-VERIFIED: the exact boolean gate that decides ad serving still needs a
 *   bytecode pass to confirm this string sits in a `()Z` gate (full jadx/REA decompile —
 *   OOMs on a <4 GB box). Run on a larger machine:
 *     "Decompile com.snapchat.android, find the method referencing
 *      skip_ad_insertion_with_null_group_id, and show whether it gates ad serving."
 *   Adjust returnType/filters to the real method before relying on this patch.
 */
object AdServingGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    filters = listOf(
        string("skip_ad_insertion_with_null_group_id"),
    ),
)
