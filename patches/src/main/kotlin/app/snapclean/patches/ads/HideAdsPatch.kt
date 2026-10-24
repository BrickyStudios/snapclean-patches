package app.snapclean.patches.ads

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.snapclean.patches.shared.Constants.COMPATIBILITY_SNAPCHAT

private const val EXTENSION_CLASS = "Lapp/snapclean/extension/ads/HideAdsExtension;"

/**
 * Hides sponsored Snaps, Discover/Spotlight ad tiles and other injected ad content.
 *
 * Strategy: the app asks an internal gate whether an ad slot should be served/rendered.
 * We rewrite that gate so it always reports "no ad", which drops the ad from the UI
 * without touching the surrounding layout.
 */
@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
    description = "Removes sponsored Snaps and ad tiles in Stories, Discover and Spotlight.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_SNAPCHAT)

    extendWith("extensions/extension.mpe")

    execute {
        // Override the ad-serving gate's return value with the extension result (false).
        AdServingGateFingerprint.method.addInstructions(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS->shouldServeAds()Z
                move-result v0
                return v0
            """,
        )
    }
}
