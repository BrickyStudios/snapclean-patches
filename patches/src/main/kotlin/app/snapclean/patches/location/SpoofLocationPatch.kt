package app.snapclean.patches.location

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.snapclean.patches.shared.Constants.COMPATIBILITY_SNAPCHAT

private const val EXTENSION_CLASS = "Lapp/snapclean/extension/location/SpoofLocationExtension;"

/**
 * Lets you place yourself anywhere on the map. When enabled, every Location the app
 * consumes is rewritten to the coordinates you configured (Snap Map, geofilters, etc.).
 *
 * Strategy: Snapchat receives device fixes as android.location.Location objects. We hook
 * the method that delivers a fresh Location and pass it to the extension, which rewrites
 * its latitude/longitude in place when spoofing is on. Disabled by default.
 *
 * Note: this changes the location Snapchat *uses*; it does not forge device GPS hardware
 * or defeat server-side checks. Treat it as a privacy / convenience toggle.
 */
@Suppress("unused")
val spoofLocationPatch = bytecodePatch(
    name = "Spoof location",
    description = "Set a custom location anywhere in the world for Snap Map and geofilters.",
    default = false,
) {
    compatibleWith(COMPATIBILITY_SNAPCHAT)

    extendWith("extensions/extension.mpe")

    execute {
        // TODO: confirm with REA which register holds the delivered Location.
        //   For an instance method `void onLocationChanged(Location l)` it is p1.
        //   The extension mutates the object in place, so no return rewrite is needed.
        LocationDeliveryFingerprint.method.addInstructions(
            0,
            "invoke-static {p1}, $EXTENSION_CLASS->overrideLocation(Landroid/location/Location;)V",
        )
    }
}
