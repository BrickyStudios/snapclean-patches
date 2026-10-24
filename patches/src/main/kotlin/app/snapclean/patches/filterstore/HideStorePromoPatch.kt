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
 * SCOPE (by project decision): this hides the *promo UI only*. It must NOT spoof an
 * active subscription. Forcing the SubscriptionInfo constructor to tier/status = 2
 * (as SnapEnhance does) would unlock a paid tier for free — that is a premium-unlock /
 * payment circumvention and is deliberately out of scope here. See ROADMAP.md.
 *
 * Strategy: find the boolean *eligibility* gate that decides whether an upsell surface
 * is shown and force it to "not eligible / don't show", leaving the free lens carousel
 * intact and unlocking nothing.
 */
@Suppress("unused")
val hideStorePromoPatch = bytecodePatch(
    name = "Hide paid filter & Snapchat+ promos (EXPERIMENTAL, WIP)",
    description = "WIP: the upsell gate is not located yet, so this will currently fail to " +
        "apply. Off by default. See Fingerprints.kt for the evidence and what's needed.",
    default = false,
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
