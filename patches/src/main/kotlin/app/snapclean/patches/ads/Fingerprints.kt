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
 * WILL NOT MATCH AS-IS: dexdump shows this string sits in a *void* method `(L…;)V`, not a
 *   boolean gate, so the `returnType = "Z"` filter below finds no match and the patch fails
 *   to apply (which is why it is EXPERIMENTAL and off by default). The real ad-serving gate
 *   is elsewhere and heavily obfuscated. Locate it with a full jadx/REA decompile on a
 *   >4 GB box, then rewrite this fingerprint and the patch body to the real method:
 *     "Decompile com.snapchat.android, find the method that decides whether an ad slot is
 *      served/rendered, show its signature and the strings/opcodes around it."
 */
object AdServingGateFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC),
    returnType = "Z",
    filters = listOf(
        string("skip_ad_insertion_with_null_group_id"),
    ),
)
