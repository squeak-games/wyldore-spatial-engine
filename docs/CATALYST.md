# Android XR Developer Catalyst — repository claim mapping

This document is the audit surface for Google reviewers: every technical claim
in the Hushwild / Wyldore catalyst pitch deck is mapped here to the file in
this repository that substantiates it. If a claim has no entry, it should not
be in the deck.

## Slide 7 — Technical Innovation

| Deck claim | Repository evidence |
|---|---|
| "Jetpack XR SceneCore" scene-graph runtime | `engine/src/main/.../SceneCoreHost.kt` — type `androidx.xr.scenecore.Session` is the live scene-graph surface; per-frame commit happens here |
| "Jetpack Projected API" head-tracked HUD surface | `engine/src/main/.../ProjectedLayer.kt` — `Anchor.SPATIAL_FIXED / HEAD_LOCKED / SURFACE_ATTACHED` intent, opacity cap, glanceability floor |
| "Compose for XR Layer" (renamed from "Compose Glimmer") | `app/build.gradle.kts` declares `androidx.xr.compose`; `MainActivity.kt` hosts the Compose tree; `ProjectedLayer` holds layout intent rather than a fictional component |
| "Procedural Audio / 6DoF acoustic panning" | `spatial-audio/src/main/.../SpatialPanner.kt` — head + source pose model, per-tick gains |

## Slide 8 — Developer Readiness

| Deck claim | Repository evidence |
|---|---|
| "Functional Phone-Side Prototype" | `:app` module builds `assemblePrototype`; `MainActivity.kt` is a working Compose entrypoint with a Health Connect bridge |
| "Spatial Engine Paradigm (ECS)" | `engine/src/main/.../EcsWorld.kt` — entity/component store, per-frame iteration in `SceneCoreHost.tickOnce()` |
| "Automated Simulation Frameworks" (`ProjectedTestRule`) | `engine/src/test/.../testing/ProjectedTestRule.kt` — JUnit `TestRule` that boots the ECS world on the JVM and drives the tick loop; `SceneCoreHostTest` runs assertions with no emulator |
| "Code & Video Evidence Available" | This repository (code) + demo video linked from the submission portal (see deck slide 13) |

## Slide 2 — The Opportunity

| Deck claim | Repository evidence |
|---|---|
| "Android Health Connect API integrations" | `app/build.gradle.kts` depends on `androidx.health.connect`; `HealthConnectBridge.kt` reads heart rate into the engine |

## Slide 9 — Privacy by Design

| Deck claim | Repository evidence |
|---|---|
| "No Accounts Required" | No login/token surface anywhere; the only ingress is Health Connect (OS-gated consent) |
| "100% Local Processing" | No HTTP client on the classpath; `docs/PRIVACY.md` provides the verification one-liner |
| "Zero Audio Storage" | `SpatialPanner.kt` exposes only pose → gains; no audio buffer is retained |

## Milestones (slide 11)

| Milestone | Repo status at this commit |
|---|---|
| 1. Engine Architecture | Scaffolded — `:engine` + `:spatial-audio` modules |
| 2. Functional Alpha | In progress — `:app` phone-side prototype builds; on-device XR pass pending prototype hardware |
| 3. Device Optimization | Pending — awaits Catalyst-provided kit Milestone 2 |
| 4. Google Play Launch | Pending — Android XR Play Store launch surface not yet GA |

## Catalyst asks (slide 12)

1. **Google prototype access** — 2× pre-release Display/Audio glass developer kits (per the deck).
2. **Direct technical mentorship** — alignment on upcoming Android XR energy-saving policies.
3. *(Optional)* Featured placement consideration on the Android XR Play Store at launch.

This repository is the substance behind the third paragraph of the closing
slide: "fully technically aligned, resource-ready, and optimized for launch-day
success." It is open-sourced in good faith as part of the application.