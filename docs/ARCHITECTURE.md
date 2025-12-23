# Architecture

The prototype is organised as a three-module Gradle project with a deliberately
small surface area. The intent is that every architectural decision named in
the catalyst deck maps to a file here.

```
wyldore-spatial-engine/
├── app/                           # :app — phone-side prototype host
│   └── src/main/kotlin/com/squeakgames/wyldore/
│       ├── WyldoreApp.kt          # Application + manual DI container
│       ├── AppContainer.kt        # Dependency wiring (no network, by design)
│       ├── MainActivity.kt        # Compose for XR entrypoint
│       └── health/
│           └── HealthConnectBridge.kt   # Android Health Connect read loop
├── engine/                        # :engine — spatial engine + SceneCore
│   └── src/
│       ├── main/kotlin/com/squeakgames/wyldore/engine/
│       │   ├── EcsWorld.kt        # Entity/component store
│       │   ├── SceneCoreHost.kt   # Per-frame ECS → SceneCore bridge
│       │   └── ProjectedLayer.kt  # Projected API HUD intent
│       └── test/kotlin/com/squeakgames/wyldore/engine/
│           ├── testing/ProjectedTestRule.kt   # Simulation harness
│           └── SceneCoreHostTest.kt           # JVM simulation tests
└── spatial-audio/                 # :spatial-audio — 6DoF panner
    └── src/main/kotlin/com/squeakgames/wyldore/audio/
        └── SpatialPanner.kt       # Head + source pose → channel gains
```

## Per-frame loop

```
HealthConnect ──►  app.MainActivity  ──►  engine.SceneCoreHost.tickOnce()
   (bpm)              (Compose)               │
                                              ├──► EcsWorld.moveTo(creature)
                                              ├──► SceneCore session commit
                                              └──► SpatialPanner.updateListenerPose()
```

*One ticking loop, one head pose, one source pose.* That is the full runtime
surface of the prototype. Everything else is data shaping.

## Why ECS (and not a 2D object graph)

The deck's slide 8 calls out the ECS transition from a 2D object graph as a
real architectural decision. The reason, briefly: on a head-mounted display the
dominant cost is per-frame allocation churn that stalls the spatial frame
pipeline, and a 2D object graph funnels every update through a single hierarchy
walk with unavoidable allocations. ECS lets the render-adjacent system iterate a
packed component array per frame, with zero allocations in the steady state, and
lets other systems (audio pan, biometric feedback, animation) read the same
component slice independently. `EcsWorld.kt` is the data half of this; the
scene-graph commit lives on the device side via `SceneCoreHost`.

## Why "Compose for XR Layer"

Slide 7 renamed the deck's "Compose Glimmer" claim to *Compose for XR Layer*.
This scaffold reads that literally: `ProjectedLayer.kt` holds the **intent**
for the projected HUD panel (anchor, opacity cap, glanceability floor,
head-track flag) and the actual Compose tree lives in the `:app` module under
`MainActivity.kt`. We do not name a fictional Compose component — we name the
public Jetpack XR Compose surface, and put our own layout intent in front of
it.

## Simulation vs. on-device

The simulation harness (`ProjectedTestRule`) is the part of the scaffold that
matters most for an open-source prototype: it lets a reviewer run
`./gradlew :engine:test` on any JVM and watch the architectural loop execute
without an emulator. The same `SceneCoreHost.tickOnce()` code path runs on the
headset with a real `Session` object instead of `null`. Switching between the
two is a constructor parameter, not a fork.