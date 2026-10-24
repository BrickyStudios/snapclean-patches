package app.snapclean.patches.filterstore

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.snapclean.patches.shared.Constants.COMPATIBILITY_SNAPCHAT

private const val EXTENSION_CLASS = "Lapp/snapclean/extension/filterstore/StorePromoExtension;"

/**
 * Hides the promotion of paid lenses/filters and Snapchat+ upsell nags: the
 * "unlock with Snapchat+" banners, the paid-filter carousel entries and the
 * recurring subscription prompts.
 *
 * Strategy: an internal gate decides whether a promo/upsell surface should be shown.
 * We rewrite it to always report "no promo", so the free lens carousel stays intact
 * while the paid upsell entries are dropped.
 */
@Suppress("unused")
val hideStorePromoPatch = bytecodePatch(
    name = "Hide paid filter & Snapchat+ promos",
    description = "Removes paid-lens upsells and recurring Snapchat+ subscription nags.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SNAPCHAT)

    extendWith("extensions/extension.mpe")

    execute {
        StorePromoGateFingerprint.method.addInstructions(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS->shouldShowPromo()Z
                move-result v0
                return v0
            """,
        )
    }
}
