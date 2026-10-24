package app.snapclean.extension.filterstore;

public final class StorePromoExtension {
    private StorePromoExtension() {}

    /**
     * Patched into the app's promo/upsell gate. Returning false hides paid-lens upsells
     * and Snapchat+ subscription nags while leaving the free lens carousel intact.
     */
    public static boolean shouldShowPromo() {
        return false;
    }
}
