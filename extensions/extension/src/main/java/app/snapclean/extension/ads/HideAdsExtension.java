package app.snapclean.extension.ads;

public final class HideAdsExtension {
    private HideAdsExtension() {}

    /**
     * Patched into the app's ad-serving gate. Returning false means "no ad is served",
     * which drops sponsored Snaps and ad tiles without disturbing the surrounding UI.
     */
    public static boolean shouldServeAds() {
        return false;
    }
}
