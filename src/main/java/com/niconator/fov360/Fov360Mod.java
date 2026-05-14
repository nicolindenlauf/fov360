package com.niconator.fov360;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Fov360Mod.MODID)
public class Fov360Mod {
    public static final String MODID = "fov360";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Fov360Mod(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, Fov360Config.SPEC);
        modEventBus.addListener(Fov360Config::onLoad);
        modEventBus.addListener(Fov360Config::onReload);

        LOGGER.info("{} initialized.", MODID);
    }

    public static void applyClientConfigNow() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.options == null) {
            return;
        }
        if (minecraft.options instanceof Fov360OptionsAccess access) {
            access.fov360$rebuildFovOption();
        }
    }
}
