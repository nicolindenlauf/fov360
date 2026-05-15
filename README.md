# FOV360 (NeoForge)

FOV360 is a lightweight client-side NeoForge mod for Minecraft 1.21.1.

## Mod Overview
- Raises the FOV slider cap from 110 to 360.
- Keeps option serialization stable for extended FOV values.
- Adds configurable frustum culling margin to reduce visual pop-in at high FOV or with shader effects.
- Includes a compatibility mixin for Sodium's viewport culling path.
- Applies the same culling margin to Distant Horizons LOD terrain when Distant Horizons is present.

## Configuration
Config file: `config/fov360.properties`

Available options:
- `maxFov` (int, default `360`, clamped to `110..360`)
- `cullingMarginBlocks` (int, default `40`, clamped to `0..256`)
- `distantHorizonsCullingMarginBlocks` (int, default `80`, clamped to `0..256`)

## Compatibility
- Minecraft: `1.21.1`
- Loader: NeoForge `21.1.228+`
- Java: `21`
- Required companion mod: `Sodium` (client-side dependency)
- Optional companion mod: `Distant Horizons`

## Compatibility And Quality Checklist (Reusable)
Use this checklist when standardizing other projects:

- Unique package namespace per mod (for example `com.<author>.<modid>`), not just author-only namespace.
- Clean mod metadata (`neoforge.mods.toml`): `displayURL`, `issueTrackerURL`, authors, description, and explicit dependencies.
- License clarity and consistency:
  - Add a real `LICENSE` file in repo root.
  - Match `mod_license` in `gradle.properties` to that license identifier.
  - Ensure README license section matches both metadata and `LICENSE`.
  - Only license your own code/assets; do not re-license third-party dependencies.
- CI-ready Gradle wrapper:
  - `gradlew` tracked as executable (`100755`) so GitHub Actions can run it.
  - No machine-specific `org.gradle.java.home` committed.
  - Java version pinned for reproducibility (`gradle/gradle-daemon-jvm.properties` with toolchain version).
- GitHub Actions build workflow passing on push/PR.
- Build artifacts uploaded in CI (`build/libs/*.jar`) for download from workflow runs.
- Remove leftovers from reused templates/projects (old classes/resources, stale docs, unused run/debug files).
- Keep mixins minimal and targeted:
  - One mixin per actual render path/implementation needed.
  - Remove fallback/compat paths you no longer support.
- Configuration model clearly chosen and documented:
  - `ModConfigSpec` if you want Configured in-game integration.
  - Custom file config if you prefer startup-only/simple behavior.
- Logs useful but minimal: keep startup/config confirmation logs, remove temporary debug spam before release.
- Final verification before release:
  - Local `./gradlew build` passes.
  - GitHub Actions build passes.
  - Runtime smoke test in-game confirms core features and config behavior.

## License
CC0-1.0 (public domain dedication).
