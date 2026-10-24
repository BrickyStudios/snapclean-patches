# SnapClean — Ad-Free Snapchat Patches for Morphe (No Ads, No Paid-Filter Nags, Free Custom Location)

**SnapClean is an open-source patch bundle that removes ads from Snapchat on Android, hides the paid-filter and Snapchat+ upsell nags, and lets you set your location anywhere in the world — applied with [Morphe](https://morphe.software/), the free open-source app patcher.** No root, no subscription, no telemetry. You patch your own legally obtained Snapchat APK locally and keep using your own account.

> Tired of sponsored Snaps in every Story, the constant "unlock with Snapchat+" banners, and paid lenses shoved in your face? SnapClean strips the ads and the upsells back out, and adds a privacy toggle to place your Snap Map pin wherever you want.

## Add these Snapchat patches to Morphe

Click to add this source in Morphe Manager:

👉 **https://morphe.software/add-source?github=BrickyStudios/snapclean-patches**

Then select **Snapchat** and apply the patches below. New to Morphe? See [How Morphe works](https://github.com/MorpheApp/morphe-documentation).

## Features

| Patch | What it does |
|-------|--------------|
| **Hide ads** | Removes sponsored Snaps and ad tiles in Stories, Discover and Spotlight. *(on by default)* |
| **Hide paid filter & Snapchat+ promos** | Removes paid-lens upsells and the recurring Snapchat+ subscription nags, keeping the free lens carousel. *(on by default)* |
| **Spoof location** | Set a custom latitude/longitude anywhere on Earth for Snap Map and geofilters. *(off by default)* |

<!-- PATCHES_START -->
<!-- The detailed patch list is generated automatically on the first release. -->
<!-- PATCHES_END -->

## Why SnapClean

- **Ad-free Snapchat** — no sponsored Snaps, no ad tiles, a clean feed.
- **No upsell nags** — the "get Snapchat+" and paid-lens prompts are gone.
- **Free custom location** — a built-in fake-location / GPS-spoof toggle for Snap Map, no separate mock-location app needed.
- **Open source & private** — GPLv3, patched locally on your device, nothing is uploaded, your account stays yours.
- **No root required** — Morphe repackages the app; install it like any other APK.

## How it works

Snapchat is closed-source, so SnapClean ships *patches*, not a modified app. [Morphe](https://morphe.software/) (an open-source fork of the ReVanced patcher) applies these patches to the official Snapchat APK on your own machine. SnapClean never redistributes Snapchat itself — you supply the original APK.

1. Install **Morphe Manager** (Android) or **Morphe Desktop**.
2. Add this source: `BrickyStudios/snapclean-patches`.
3. Supply the official Snapchat APK (from [APKMirror](https://www.apkmirror.com/apk/snap-inc/snapchat/)).
4. Pick the SnapClean patches and patch. Install the result.

## Setting a custom location

The **Spoof location** patch is off by default. Set your coordinates before building:

- Edit the defaults in [`SpoofLocationExtension.java`](extensions/extension/src/main/java/app/snapclean/extension/location/SpoofLocationExtension.java) — `ENABLED`, `LATITUDE`, `LONGITUDE` — then rebuild, **or**
- Call `SpoofLocationExtension.configure(true, lat, lon)` at runtime to move the pin without rebuilding.

It rewrites the location Snapchat *uses*; it does not forge GPS hardware or defeat server-side checks. Treat it as a privacy/convenience toggle.

## Building & contributing

SnapClean is built on the official [`morphe-patches-template`](https://github.com/MorpheApp/morphe-patches-template).

```bash
./gradlew buildAndroid
# → patches/build/libs/patches-*.mpp — apply with Morphe Desktop.
```

The patches match Snapchat internals by **fingerprints** in each feature's `Fingerprints.kt`, anchored to evidence from **Snapchat 14.29.0.50** (versionCode 322522):

| Patch | Fingerprint status |
|-------|--------------------|
| Spoof location | **Method-verified** — `onLocationChanged(Landroid/location/Location;)V` confirmed in the APK (classes2/3.dex). |
| Hide ads | Anchored to the confirmed string `skip_ad_insertion_with_null_group_id`; exact boolean-gate method not yet verified. |
| Hide promos | Anchored to the confirmed flag `PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED`; exact gate not yet verified. |

Snapchat is heavily obfuscated and changes every release, so the two "not yet verified" gates need a full bytecode decompile to confirm the method shape (and runtime testing) before a stable release. Use [REA](https://github.com/morluto/rea) + jadx on a machine with ≥ 4 GB RAM:

> *"Decompile com.snapchat.android, find the method referencing `skip_ad_insertion_with_null_group_id`, and show whether it gates ad serving."*

See the [Morphe fingerprinting docs](https://github.com/MorpheApp/morphe-patcher/blob/main/docs/2_2_1_fingerprinting.md). Make changes on the `dev` branch; releases are cut by `release.yml` (do not edit generated files by hand).

## FAQ

**Is this a Snapchat ad blocker?** Yes — it removes in-app Snapchat ads by patching the app, rather than filtering network traffic.

**Does it need root?** No.

**Will my account get banned?** This is unofficial. Patching any closed-source app carries risk; use a stable, confirmed app version and apply at your own risk.

**Is it free?** Yes, GPLv3, forever. SnapClean never asks for payment.

**Which Snapchat versions are supported?** Fingerprinted against **14.29.0.50** (universal APK); newer builds are allowed experimentally — see [`Constants.kt`](patches/src/main/kotlin/app/snapclean/patches/shared/Constants.kt).

## Keywords

Snapchat no ads · remove Snapchat ads · Snapchat ad blocker Android · ad-free Snapchat · Snapchat mod · Snapchat patches · Morphe Snapchat · Snapchat without Snapchat+ · Snapchat fake location · Snapchat spoof location · Snap Map location changer · open source Snapchat patcher · ReVanced-style Snapchat.

## Legal

SnapClean is an independent open-source project. It is **not** affiliated with, authorized or endorsed by Snap Inc. or the Morphe project. "Snapchat" and "Snap" are trademarks of Snap Inc., used here only descriptively to identify the app these patches are compatible with. SnapClean distributes **patches only** — never Snapchat itself. You are responsible for obtaining Snapchat lawfully and for complying with its terms.

## License

SnapClean Patches are licensed under the [GNU General Public License v3.0](LICENSE). See [`NOTICE`](NOTICE) for the Morphe naming terms.
