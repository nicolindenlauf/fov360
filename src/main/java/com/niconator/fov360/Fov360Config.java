package com.niconator.fov360;

import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class Fov360Config {
    private static final int MIN_MAX_FOV = 110;
    private static final int DEFAULT_MAX_FOV = 360;
    private static final int ABSOLUTE_MAX_FOV = 360;

    private static final int MIN_CULLING_MARGIN_BLOCKS = 0;
    private static final int DEFAULT_CULLING_MARGIN_BLOCKS = 40;
    private static final int MAX_CULLING_MARGIN_BLOCKS = 256;

    private static final int DEFAULT_DISTANT_HORIZONS_CULLING_MARGIN_BLOCKS = DEFAULT_CULLING_MARGIN_BLOCKS * 2;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static final ModConfigSpec.IntValue MAX_FOV = BUILDER
        .comment("Maximum FOV slider value.", "Range: 110..360")
        .defineInRange("maxFov", DEFAULT_MAX_FOV, MIN_MAX_FOV, ABSOLUTE_MAX_FOV);

    static final ModConfigSpec.IntValue CULLING_MARGIN_BLOCKS = BUILDER
        .comment("Extra frustum culling margin in blocks.", "Range: 0..256")
        .defineInRange(
            "cullingMarginBlocks",
            DEFAULT_CULLING_MARGIN_BLOCKS,
            MIN_CULLING_MARGIN_BLOCKS,
            MAX_CULLING_MARGIN_BLOCKS
        );

    static final ModConfigSpec.IntValue DISTANT_HORIZONS_CULLING_MARGIN_BLOCKS = BUILDER
        .comment("Extra Distant Horizons LOD frustum culling margin in blocks.", "Range: 0..256")
        .defineInRange(
            "distantHorizonsCullingMarginBlocks",
            DEFAULT_DISTANT_HORIZONS_CULLING_MARGIN_BLOCKS,
            MIN_CULLING_MARGIN_BLOCKS,
            MAX_CULLING_MARGIN_BLOCKS
        );

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static volatile int cachedMaxFov = DEFAULT_MAX_FOV;
    private static volatile int cachedCullingMarginBlocks = DEFAULT_CULLING_MARGIN_BLOCKS;
    private static volatile int cachedDistantHorizonsCullingMarginBlocks =
        DEFAULT_DISTANT_HORIZONS_CULLING_MARGIN_BLOCKS;

    private Fov360Config() {
    }

    public static void onLoad(final ModConfigEvent.Loading event) {
        refreshCache(event);
    }

    public static void onReload(final ModConfigEvent.Reloading event) {
        refreshCache(event);
    }

    private static void refreshCache(ModConfigEvent event) {
        if (!Fov360Mod.MODID.equals(event.getConfig().getModId())) {
            return;
        }

        cachedMaxFov = MAX_FOV.get();
        cachedCullingMarginBlocks = CULLING_MARGIN_BLOCKS.get();
        cachedDistantHorizonsCullingMarginBlocks = DISTANT_HORIZONS_CULLING_MARGIN_BLOCKS.get();

        Fov360Mod.LOGGER.info(
            "Config updated: maxFov={}, cullingMarginBlocks={}, distantHorizonsCullingMarginBlocks={}",
            cachedMaxFov,
            cachedCullingMarginBlocks,
            cachedDistantHorizonsCullingMarginBlocks
        );

        Fov360Mod.applyClientConfigNow();
    }

    public static int getMaxFov() {
        return cachedMaxFov;
    }

    public static double getCullingMarginBlocks() {
        return cachedCullingMarginBlocks;
    }

    public static double getDistantHorizonsCullingMarginBlocks() {
        return cachedDistantHorizonsCullingMarginBlocks;
    }

    public static double getFovOptionScale() {
        return cachedMaxFov - 70.0;
    }
}
