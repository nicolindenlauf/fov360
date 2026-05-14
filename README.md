# FOV360 (NeoForge)

FOV360 is a lightweight client-side NeoForge mod for Minecraft 1.21.1.

## What it does
- Raises the FOV slider cap from 110 to 360.
- Keeps option serialization stable for extended FOV values.
- Adds configurable frustum culling margin to reduce visual pop-in at high FOV or with shader effects.
- Includes a compatibility mixin for Sodium's viewport culling path.

## Configuration
Config file: `config/fov360.properties`

Available options:
- `maxFov` (int, default `360`, clamped to `110..360`)
- `cullingMarginBlocks` (double, default `40.0`, clamped to `0.0..256.0`)

## Compatibility
- Minecraft: `1.21.1`
- Loader: NeoForge `21.1.228+`
- Java: `21`

## License
All Rights Reserved.
