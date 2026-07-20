# wyldore-spatial-engine

> A sensor‑driven, ambient audio‑first creature‑companion for Android XR
> wearables — **Hushwild**.
> Submitted in support of the **Android XR Developer Catalyst Program**.

This repository implements the full Hushwild companion pipeline: environmental
sensors (microphone, IMU, geofence) → composite stress index → 5 companion
emotional states → spatial audio → user gesture interaction → bond progression
across sessions.

The Jetpack XR stack (SceneCore, Projected API, Compose for XR) is wired
end‑to‑end through a per‑frame ECS tick loop, with a JVM‑runnable simulation
harness (`ProjectedTestRule`) standing in for on‑device rendering.

---

## Module layout

| Module | Purpose | Deck reference |
|---|---|---|
| `:app` | Phone‑side Compose for XR host + sensor foreground service | Slide 2 (Health Connect), Slide 8 (phone prototype), Slide 10 (sensor loop) |
| `:engine` | ECS spatial world, SceneCore session, companion state, bond progression | Slide 7 (SceneCore, ECS), Slide 11 (bonds) |
| `:spatial-audio` | 6DoF acoustic panner — head + source pose model | Slide 7 (Procedural Audio) |

Architecture detail for each module is in
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Stack

- **Kotlin 2.0.x** + **AGP 8.7**, JDK 17
- **Jetpack XR SceneCore** (`androidx.xr.scenecore`) — spatial scene graph runtime
- **Jetpack XR Compose** (`androidx.xr.compose`) — the "Compose for XR Layer"
- **Jetpack XR Projected API** — head‑tracked, glanceable HUD panel
- **Android Health Connect** (`androidx.health.connect`) — ambient biometric loop
- **Play Services Location** (`play-services-location`) — geofencing sanctuary zones
- **Android AudioRecord** — ambient RMS extraction (ephemeral, no storage)
- **Android Sensor API** (`android.hardware.Sensor`) — IMU step cadence
- **Coroutines** for the per‑frame tick scheduling and sensor flows

Versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml).
XR artifact coordinates track the public Android XR alpha channel and should be
bumped to the stable coordinates when they release.

## Build

```
gradle wrapper            # generate the gradlew wrapper (binary jar not committed here)
./gradlew :app:assemblePrototype
./gradlew :engine:test    # runs ProjectedTestRule simulation harness
./gradlew test            # all module tests
```

The phone‑side preview can be exercised on any Android 12+ device or an
XR‑capable emulator image; the simulation tests are pure JVM (Robolectric) and
need no emulator round‑trip.

## Simulation evidence

The project has **6 test files** spanning all modules:

| Test file | What it validates |
|---|---|
| `engine/src/test/.../SceneCoreHostTest` | ECS creature init, HUD glanceability, 6DoF panning |
| `engine/src/test/.../BondProgressionTest` | Bond tier progression (Stranger→Symbiote), reset |
| `engine/src/test/.../audio/CompanionStateTest` | State enum properties (frequencies, overtones, breathing) |
| `engine/src/test/.../audio/SpatialAudioEngineTest` | State transitions, spatial position computation |
| `app/src/test/.../sensor/EnvironmentalStressIndexTest` | ESI computation, smoothing, hysteresis, reset |
| `app/src/test/.../interaction/SootheGestureHandlerTest` | Gesture state, damping timers, reset |

See **[`docs/CATALYST.md`](docs/CATALYST.md)** for the mapping from each deck
claim to the file in this repo that substantiates it.

## Privacy

No accounts, no network, no audio storage. Audio buffers are ephemeral (RMS
extraction with zero retention). See [`docs/PRIVACY.md`](docs/PRIVACY.md). There
is no network client anywhere in the dependency graph — by construction, not by
policy.

## License

Apache 2.0 — see [`LICENSE`](LICENSE).

## Status

Functional alpha — sensor pipeline, ESI, companion states, spatial audio
positioning, soothe gestures, and bond progression are all implemented and
tested. See [`docs/TIMELINE.md`](docs/TIMELINE.md) for the full milestone plan.