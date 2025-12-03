# wyldore-spatial-engine

> A work-in-progress XR prototype for **Hushwild** — an ambient audio‑first
> creature‑companion for Android XR wearables.
> Submitted in support of the **Android XR Developer Catalyst Program**.

This repository holds the spatial‑engine scaffold described in the Wyldore
catalyst pitch deck (slide 7: *Jetpack XR SceneCore · Projected API ·
Compose for XR*; slide 8: *Functional Phone‑Side Prototype · Automated
Simulation Frameworks · Code & Video Evidence*).

It is intentionally honest about its scope: at this commit it is an
**architectural scaffold** — a compiling multi‑module Kotlin/Gradle project
that wires the named Jetpack XR stack end‑to‑end, with a JVM‑runnable
simulation harness (`ProjectedTestRule`) standing in for on‑device rendering.
It is not yet the shipping creature experience; it is the evidence that we
already know how to build it. See
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the module structure,
per‑frame loop, and design rationale.

---

## Module layout

| Module | Purpose | Deck reference |
|---|---|---|---|
| `:app` | Phone‑side Compose for XR host + Android Health Connect bridge | Slide 2 (Health Connect), Slide 8 (phone prototype) |
| `:engine` | ECS spatial world, Jetpack XR SceneCore session, Projected‑API HUD layer | Slide 7 (SceneCore, Projected API), Slide 8 (ECS) |
| `:spatial-audio` | 6DoF acoustic panner — head + source pose model | Slide 7 (Procedural Audio) |

Architecture detail for each module is in
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Stack

- **Kotlin 2.0.x** + **AGP 8.7**, JDK 17
- **Jetpack XR SceneCore** (`androidx.xr.scenecore`) — spatial scene graph runtime
- **Jetpack XR Compose** (`androidx.xr.compose`) — the "Compose for XR Layer"
- **Jetpack XR Projected API** — head‑tracked, glanceable HUD panel
- **Android Health Connect** (`androidx.health.connect`) — ambient biometric loop
- **Coroutines** for the per‑frame tick scheduling

Versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).
XR artifact coordinates track the public Android XR alpha channel and should be
bumped to the stable coordinates when they release.

## Build

```
gradle wrapper            # generate the gradlew wrapper (binary jar not committed here)
./gradlew :app:assemblePrototype
./gradlew :engine:test    # runs ProjectedTestRule simulation harness
```

The phone‑side preview can be exercised on any Android 12+ device or an
XR‑capable emulator image; the simulation tests are pure JVM (Robolectric) and
need no emulator round‑trip.

## Simulation evidence

`engine/src/test/.../SceneCoreHostTest` drives the per‑frame loop on the JVM
and asserts:

- the creature entity is initialised at a known spatial anchor (~2 m in front
  of the listener),
- the projected HUD stays glanceable across the ambient‑light range the
  on‑device pass expects,
- the 6DoF panner tracks the creature pose each tick.

See **[`docs/CATALYST.md`](docs/CATALYST.md)** for the mapping from each deck
claim to the file in this repo that substantiates it.

## Privacy

No accounts, no network, no audio storage. See
[`docs/PRIVACY.md`](docs/PRIVACY.md). There is no network client anywhere in
the dependency graph — by construction, not by policy.

## License

Apache 2.0 — see [`LICENSE`](LICENSE).

## Status

Prototype scaffold — open in good faith as part of the catalyst application.
Committed 2025‑12‑23. Future commits track the milestone plan in
[`docs/TIMELINE.md`](docs/TIMELINE.md) and
[`docs/CATALYST.md`](docs/CATALYST.md).