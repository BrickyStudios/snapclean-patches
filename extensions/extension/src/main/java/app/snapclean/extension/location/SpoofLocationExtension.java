package app.snapclean.extension.location;

import android.location.Location;

/**
 * Rewrites every {@link Location} the app consumes to a custom position.
 *
 * Runtime config: {@link #ENABLED}, {@link #LATITUDE} and {@link #LONGITUDE} are the
 * defaults compiled into the patch. Edit them and rebuild to place yourself anywhere,
 * or call {@link #configure(boolean, double, double)} at runtime (e.g. from a debugger
 * or a future settings patch) to change the spot without rebuilding.
 *
 * shortcut: coordinates are source constants, not a settings screen. To make them
 *   editable in-app, wire this to Morphe's shared settings library
 *   (app.morphe.extension.shared.settings.*) and read ENABLED/LAT/LON from a
 *   FloatSetting/BooleanSetting instead. Do that when a UI toggle is wanted.
 */
public final class SpoofLocationExtension {
    private SpoofLocationExtension() {}

    // --- Default config: edit these, or override at runtime via configure(). ---
    private static volatile boolean ENABLED = false;
    private static volatile double LATITUDE = 40.7580;   // Times Square, New York.
    private static volatile double LONGITUDE = -73.9855;

    public static void configure(boolean enabled, double latitude, double longitude) {
        ENABLED = enabled;
        LATITUDE = latitude;
        LONGITUDE = longitude;
    }

    /** Mutates the delivered Location in place when spoofing is enabled. */
    public static void overrideLocation(Location location) {
        if (!ENABLED || location == null) {
            return;
        }
        location.setLatitude(LATITUDE);
        location.setLongitude(LONGITUDE);
        // Drop accuracy/bearing/speed so stale real-device values don't contradict the spoof.
        location.removeAccuracy();
        location.removeBearing();
        location.removeSpeed();
    }
}
