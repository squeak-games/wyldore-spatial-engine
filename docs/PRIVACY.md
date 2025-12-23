# Privacy by design

The deck's slide 9 claims *No accounts · 100% local compute · Zero audio
storage*. This document explains how this repository enforces those claims by
construction, not by convention.

## No accounts

There is no account model in the codebase. The single data ingress is the
Android Health Connect read surface (`HealthConnectBridge.kt`), gated by the
operating system's own consent UI — Wyldore never prompts for or stores a
credential. There is no login screen, no token, no user identifier of any kind.

## 100% local compute

`AppContainer` is the complete dependency graph of the prototype. Reading it
top to bottom yields four wired components:

- `EcsWorld` (pure Kotlin)
- `SpatialPanner` (pure Kotlin)
- `HealthConnectBridge` (local Android Health Connect read)
- `SceneCoreHost` (SceneCore, an on-device SDK)

There is no HTTP client on the classpath. There is no `okhttp`, no `retrofit`,
no `.volley`, no `HttpURLConnection` call site anywhere in `:app`, `:engine`,
or `:spatial-audio`. The absence can be verified mechanically:

```sh
git grep -E "OkHttp|Retrofit|HttpURLConnection|okhttp3|UrlConnection" -- \
    app engine spatial-audio
# (no matches expected)
```

If a future contributor needs network surfacing for some reason, they must
answer this document first.

## Zero audio storage

`SpatialPanner.kt` is the only audio-adjacent class in the prototype. Its public
surface takes head and source poses as inputs and produces a pair of float
gains as output. It explicitly does **not** retain any audio buffer:

- there is no `AudioRecord` call site,
- there is no `byte[]` or `ShortArray` audio field,
- there is no file at `cacheDir` or `filesDir` that holds samples.

The simulation test (`SceneCoreHostTest`) relies on this property and will fail
loudly if someone adds a retained buffer.

## Audit checklist before each release tag

- [ ] `git grep` network libraries → no matches
- [ ] `HealthConnectBridge` only reads; no `insert`/`update` calls
- [ ] `SpatialPanner` exposes no buffer getter
- [ ] No new permission in `AndroidManifest.xml` beyond the Health Connect
      read permission already declared
- [ ] `AppContainer` still compiles with no DI framework

This file is the source of truth for the "Privacy by Design" slide. If the deck
moves, this file moves first.