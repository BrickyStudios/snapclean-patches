# SnapClean — Reverse-Engineering Findings (Snapchat 14.29.0.50 / 322522)

Working notes from a full jadx decompile of the universal APK
(`sha256 edeb2c4a…35e5`). Decompiled with **jadx 1.5.6** (`--no-res
--no-debug-info --show-bad-code`) → 71,694 `.java` files, 331 MB, 437 classes
failed (anti-decompile stubs). These are *leads*, not confirmed gates; every
gate must still be confirmed by xref tracing **and on-device testing** before a
patch is flipped to `default = true`.

## Reproduce
```
jadx -d <out> --no-res --no-debug-info --show-bad-code <apk>
grep -rn "<anchor>" <out>/sources
```

## Hide promos / Snapchat+ upsell  (filterstore patch, HIDE-ONLY)

- **Config flag class:** `sources/defpackage/k4i0.java` — an (enum-style) table
  of Snapchat+ config flags read via `zci0.c("KEY", default)`. Confirms the
  anchor `PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED` is a **config constant, not a
  gate** (as the fingerprint comment warned). Neighbouring flags worth knowing:
  `ALWAYS_SHOW_UPSELLS`, `PLUS_UNIFIED_PAYWALL_ENABLED`,
  `PLUS_HIDE_UNPURCHASABLE_UPSELLS`, and many `*_UPSELL_*` entries.
- **Eligibility surfaces:** `UpsellEligibility` appears in
  `defpackage/{n2i,y0h,kbc,qdw0,spw0}.java`. `n2i` is a gRPC stub
  (`/subscription.shop.SubscriptionShop/CheckCreatorSubscriptionProfileUpsellEligibility`);
  `y0h` is a large boolean-state holder; `kbc` carries
  `arrivalNotifPushUpsellEligibility`. The real UI gate is a boolean method that
  **consumes** these, not these classes themselves.
- **Next step:** `get_xrefs_to_field` / `get_xrefs_to_method` on the `k4i0`
  upsell flags and on `y0h`'s booleans → find the `()Z` method that decides
  whether an upsell/paywall surface renders; force it false. **Do NOT** touch
  `SubscriptionInfo` tier/status (that would spoof a paid subscription — a
  deliberate non-goal).

## Hide ads  (ads patch)

- **Real package (not obfuscated):** `com/snap/ads/{api,base,core,core/lib,attachments}`.
  `com/snap/ads/core/lib/db/RemoveAdServeItemDurableJob.java` present.
- **Sponsored-item carriers** (`isSponsored` field, found via `isSponsored=` in
  generated `toString`): `defpackage/{v72,n52,ac20,ji4,tdc0,j5s0,df3}.java`
  plus `com/snap/lenses/sponsoredar/…`.
- **Anchor `skip_ad_insertion_with_null_group_id` is NOT in the decompiled
  sources** — it lives in one of the 437 failed classes; read it via smali
  (`get_method_smali` / jadx smali) if needed. Per the fingerprint note it sits
  in a `void` method, so it is not the gate anyway.
- **Next step:** `get_xrefs_to_field` on the `isSponsored` field of the carrier
  classes → find where the Opera page/story list is built and a sponsored item
  is inserted; filter it out there (the real ad-serving decision), rather than
  chasing the string.

## Config-flag layer (both features share one infrastructure)

Both features are driven by server-config flag **enums implementing `k0g`**, read
through `zci0.c("KEY", default)`:

- **Paywall/upsell:** `defpackage/k4i0.java` — keys `ALWAYS_SHOW_UPSELLS`,
  `PLUS_UNIFIED_PAYWALL_ENABLED`, `PLUS_HIDE_UNPURCHASABLE_UPSELLS`, many
  `*_UPSELL_*`. **39 consumer classes** read it (e.g. `bwv, hrb0, p5i0, cle,
  zci, e4y, uoe, nvc …`).
- **Ads:** `defpackage/r4x.java` — keys `AD_INSERTION`, `AD_SERVE`,
  `ADS_INSERTION`, `AD_INSERTION_ASYNC_SCHEDULER`, `ADS_TILE_INSERT_GARM_RISK`,
  `AD_INSERTION_*`. **6 consumer classes** (`yqq, r2d0, c0w, vtc0, c4x`).

These config values are the most **stable, fingerprint-able** anchors (the key
strings survive obfuscation). But they are sub-feature toggles — Snapchat ships
**no master "ads off" flag** — so forcing a config read false may only change a
variant, not remove all ads.

## Honest ceiling of static analysis

The exact `()Z` runtime gate — the method that decides "render this upsell" or
"insert this sponsored snap into the Opera page list" — sits in the obfuscated
consumer classes above (method names are `a`/`H`). Picking the *one* correct
method with confidence in R8-obfuscated bytecode is not reliable from static
reads alone; it needs **dynamic confirmation** (Frida hook on a device while
reproducing the behaviour), exactly as the patch fingerprints' own comments and
`CONTRIBUTING.md` prescribe ("…and runtime testing").

## Status
- [x] Toolchain (REA + jadx-headless-mcp + JDK 21 + jadx 1.5.6 CLI) verified.
- [x] Full decompile to disk → instant grep.
- [x] Paywall config enum `k4i0` + 39 consumers located.
- [x] Ad-insertion config enum `r4x` (same `k0g` infra) + 6 consumers located.
- [x] Ads package `com.snap.ads.*` + sponsored carriers located.
- [ ] Confirm the exact `()Z` gate in a consumer class — **needs on-device Frida
      tracing** (REA `instrument_with_frida` against a rooted/patched device).
- [ ] Rewrite `Fingerprints.kt` + patch bodies to the confirmed method.
- [ ] Build with Morphe + **verify on a device** before `default = true`.

## Next tool step (when a test device is available)
Hook the consumer methods with Frida and watch which boolean controls the
surface, e.g. enumerate methods of the 6 `r4x` consumers / the `k4i0` consumers,
hook their `Z`-returning methods, reproduce an ad / an upsell, and see which flip
changes the UI. That turns one of these candidates into a verified fingerprint.
