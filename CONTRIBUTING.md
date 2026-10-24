# Contributing to SnapClean — developer & reverse-engineering playbook

This is everything needed to build SnapClean and finish the two work-in-progress
patches. It bakes in the gotchas already hit so you don't rediscover them. The hard
part (locating Snapchat's obfuscated ad/upsell gates) needs a machine with **≥ 8 GB
free RAM** and an Android **device/emulator** to confirm the result — a small box
OOMs on a full Snapchat decompile.

## 1. Prerequisites

- **JDK 21** (Temurin/Eclipse works).
- **Android SDK** with `platforms;android-36` and `build-tools;36.0.0` (the extension
  module uses `compileSdk = 36`). Set `ANDROID_HOME`.
- **Node 22+ / 24+ / 26+** (only for REA, the RE tool).
- A **GitHub Personal Access Token with the `read:packages` scope** — the Morphe
  Gradle plugin is published to GitHub Packages. See Morphe's setup docs:
  https://github.com/MorpheApp/morphe-documentation and
  https://github.com/MorpheApp/morphe-patcher/blob/main/docs/2_1_setup.md
  Put it in `~/.gradle/gradle.properties` as `gpr.user=<you>` / `gpr.key=<token>`,
  or export `GITHUB_ACTOR` / `GITHUB_TOKEN`.

## 2. Build the patch bundle

```bash
export JAVA_HOME=/path/to/jdk-21
export ANDROID_HOME=/path/to/android-sdk
export GITHUB_ACTOR=<you> GITHUB_TOKEN=<read:packages PAT>
./gradlew buildAndroid
# → patches/build/libs/patches-<version>.mpp  — apply with Morphe Desktop/Manager.
```

### No Morphe-registry PAT? Build the toolchain from source into mavenLocal

This is how the bundle was built here without a `read:packages` token:

```bash
# 1) patcher + plugin @ v2.0.0 → mavenLocal, with signing disabled (GPG not needed)
cat > /tmp/nosign.init.gradle.kts <<'KTS'
allprojects { tasks.matching { it.name.startsWith("sign") }.configureEach { enabled = false } }
KTS
for repo in morphe-patcher morphe-patches-gradle-plugin; do
  git clone --depth 1 --branch v2.0.0 https://github.com/MorpheApp/$repo
  (cd $repo && ./gradlew -I /tmp/nosign.init.gradle.kts -x test publishToMavenLocal)
done
# 2) add mavenLocal() as the FIRST repo in pluginManagement.repositories in
#    settings.gradle.kts, and set GITHUB_ACTOR/GITHUB_TOKEN to any non-empty value
#    (the plugin calls .get() on them even though the registry it configures is a dummy).
./gradlew buildAndroid
```

## 3. Set up REA (to find the obfuscated gates)

```bash
npx rea-agents setup --client claude_code        # registers REA's MCP + skill
# download jadx-headless-mcp 0.7.1 and point REA at it:
#   https://github.com/1013503897/jadx-headless-mcp/releases/tag/v0.7.1
export REA_JADX_MCP_JAR=/abs/path/jadx-headless-mcp-0.7.1-all.jar
rea doctor --json      # jadx should report healthy
```

Target APK: **Snapchat 14.29.0.50, versionCode 322522**, the universal APK
(arm64-v8a + armeabi-v7a, nodpi) from APKMirror. Match this version to reuse the
evidence below. SnapClean never ships Snapchat itself — download your own.

## 4. The open problems (what your good PC is for)

The patches compile and the bundle builds; two features need their real hook located.
`com.snapchat.android` is heavily obfuscated (method names like `a`, `H`), so plaintext
string greps find *constants*, not the gate — you need a real decompile.

### a) Hide ads — `patches/.../ads/`
Snapchat ads are **sponsored snaps injected into the "Opera" viewer**, not a simple
boolean. Known dead-end: `skip_ad_insertion_with_null_group_id` sits in a *void* method,
not a `()Z` gate. Find where the Opera page/story list is built and filter sponsored items.
REA/Claude prompt:
> "Decompile com.snapchat.android. Find where the Opera page view controller builds its
>  list of pages/snaps and where a sponsored/ad snap is inserted. Show the method that
>  decides to insert an ad and the class that marks a snap as sponsored, with evidence."

### b) Hide paid-filter / Snapchat+ nags — `patches/.../filterstore/` (HIDE-ONLY)
Force a boolean **upsell-eligibility** method to return false. Known dead-end:
`PLUS_UNIFIED_PAYWALL_PROFILE_ENABLED` is a `<clinit>` constant, not a gate.
> "Decompile com.snapchat.android. Find the boolean method that decides whether a
>  Snapchat+ paywall / paid-lens upsell banner is shown (an *_UpsellEligibility-style
>  check). Show its signature — NOT the SubscriptionInfo constructor."

**Hard rule:** do **not** spoof an active subscription (patching the `SubscriptionInfo`
constructor to tier/status = 2, as SnapEnhance does). That unlocks a paid tier for free —
a premium-unlock / payment circumvention and a deliberate non-goal. SnapClean removes
clutter; it unlocks nothing. See `ROADMAP.md`.

### Pattern to copy — location (already working)
`patches/.../location/` hooks Snapchat's own `onLocationChanged(Landroid/location/Location;)V`
(a verified signature) and mutates the Location in place. Note: SnapEnhance hooks the
framework `Location` getters, which a static patcher like Morphe **cannot** do — hooking
Snapchat's own method is the correct equivalent.

Once you locate a gate: update the feature's `Fingerprints.kt` and patch body, flip the
patch back to `default = true`, `./gradlew buildAndroid`, apply with Morphe, and confirm
on a device.

## 5. Dev workflow & releases

- Work on the `dev` branch; use semantic commits (`feat:` / `fix:` / `chore:`).
- Releases are cut by `release.yml` — **do not** hand-edit `patches-list.json`,
  `patches-bundle.json`, or `CHANGELOG.md`.
- The workflows are staged in [`ci/workflows/`](ci/README.md); a maintainer with a
  `workflow`-scoped token moves them into `.github/workflows/` to enable releases.

GPLv3. See [`NOTICE`](NOTICE) for the Morphe naming terms (don't imply Morphe authorship).
