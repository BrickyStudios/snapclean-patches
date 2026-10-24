# SnapClean roadmap — what Snapchat needs, and which patches make sense

This is a planning doc: what to build for Snapchat, cross-referenced against the
other Morphe patches (YouTube/Music/Reddit) and against the mature Snapchat mod
[SnapEnhance](https://github.com/rathmerdominik/SnapEnhance) / SE-Extended.

## The one fact that decides feasibility

**Morphe is a static bytecode patcher (a ReVanced fork): it rewrites the Snapchat
APK's own code and bundles an extension.** It **cannot** do what SnapEnhance does —
SnapEnhance is an **Xposed module** that hooks *any* method at runtime, including
Android framework classes (`android.location.Location.getLatitude()` etc.).

Consequences:
- Framework-level tricks (hook `Location` getters, `LocationManager.isProviderEnabled`)
  are **not portable** — we must instead hook Snapchat's *own* methods that receive/
  use the data.
- Features that need per-event runtime hooks and heavy UI injection (stealth mode,
  message logger) are possible but need deep, per-release fingerprinting of obfuscated
  code — expensive to maintain.
- YouTube's big wins (hiding Litho UI components, SponsorBlock) rely on YouTube's
  Litho framework. **Snapchat does not use Litho**, so those don't transfer at all.

## Tier 1 — feasible now, high value (build these next)

| Feature | Why | Approach (static-patch) | Status |
|---------|-----|-------------------------|--------|
| **Spoof location** | The requested feature | Hook Snapchat's `onLocationChanged(Location)` and mutate lat/lng in place (verified signature in 14.29.0.50). SnapEnhance's framework-getter hook is Xposed-only. | ✅ Implemented |
| **Spoof Snapchat+** | Kills the "get Snapchat+" upsells **and** the paid-filter/lens nags by making the app think you're subscribed | Patch the `SubscriptionInfo` constructor to force tier=2/status=2 (SnapEnhance's proven method; anchor class found via the `PlusSubscriptionState` static field — confirmed present in the APK). | 🔜 Approach known, ready to implement |
| **Disable Play Store updates** | Stops Play Store silently replacing the patched app | Reuse Morphe's generic `all/misc/updates/DisablePlayStoreUpdatesPatch`. | 🔜 Mostly reusable |
| **Spoof app version** | Dodge Snapchat's forced-update / "unsupported version" nag screens | Analog of YouTube `misc/spoof/appversion/SpoofAppVersionPatch`: override the version string the app reports. | 🔜 Needs the version field fingerprint |
| **SnapClean settings screen** | So the location coords and toggles have an in-app UI instead of source constants | Analog of YouTube `misc/settings/SettingsPatch` + Morphe's shared settings library (`app.morphe.extension.shared.settings.*`). Infra that the other patches then plug into. | 🔜 Infrastructure |

## Tier 2 — feasible, moderate reverse-engineering

| Feature | Source of idea | Notes |
|---------|----------------|-------|
| **Disable telemetry / metrics** | SnapEnhance `DisableMetrics`; YouTube privacy patches | Find Snapchat's analytics dispatch method and no-op it. Privacy win. |
| **Unlimited snap view time** | SnapEnhance `UnlimitedSnapViewTime` | Remove the view-timer so snaps don't auto-close. |
| **Disable video length restriction** | SnapEnhance `DisableVideoLengthRestriction` | Longer camera recordings. |
| **Anti auto-save / auto-download control** | SnapEnhance `AntiAutoSave`, `AntiAutoDownload` | Control what Snapchat saves/downloads automatically. |
| **Save any snap / story (downloader)** | SnapEnhance `MediaDownloader` | Popular, but media-pipeline hooks are involved. |

## Tier 3 — hard / needs deep RE or isn't static-friendly

| Feature | Why it's hard |
|---------|---------------|
| **Hide real ads (sponsored Snaps)** | The headline goal, and the hardest. Snapchat ads are **sponsored snaps injected into the "Opera" viewer**, not a simple boolean gate (confirmed: the obvious ad strings sit in void/`<clinit>` methods, not a `()Z` gate). Needs a full decompile to find where the Opera page list is populated and filter sponsored entries. Needs a device to confirm the UI effect. |
| **Stealth mode / prevent read receipts / anonymous story view** | SnapEnhance does these with fine-grained runtime hooks of obfuscated messaging code + UI menu injection. Portable to static patches only with heavy, brittle per-release fingerprinting. |

## Not applicable (YouTube-only, no Snapchat analog)

Litho component hiding (`HideLayoutComponentsPatch`, flyout/shelves/shorts), SponsorBlock,
video quality/codec/seekbar/captions patches, Return YouTube Dislike, DeArrow — all tied
to YouTube's video player and Litho UI, which Snapchat doesn't have.

## Generic Morphe patches worth reusing for Snapchat

From `morphe-patches/.../patches/all` and `/shared`:
- `misc/updates/DisablePlayStoreUpdatesPatch` — Tier 1 above.
- `misc/clone/CloneAppPatch` — install patched Snapchat alongside the original.
- `misc/network/OverrideCertificatePinningPatch` — for anyone inspecting Snapchat traffic while developing network-based features.
- `misc/debugging/EnableAndroidDebuggingPatch` — eases development/fingerprinting.

## Suggested next step

Implement **Spoof Snapchat+** (Tier 1) — it's the biggest quick win for your "paid-filter
nags" annoyance, the approach is proven by SnapEnhance, and the anchor is already confirmed
in your APK. Then add the **settings screen** so location + future toggles get a real UI.
