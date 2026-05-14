package com.niconator.fov360.mixin;

import com.niconator.fov360.Fov360Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.viewport.Viewport", remap = false)
public class SodiumViewportCullingMarginMixin {

    /**
     * Sodium does terrain culling against render-section boxes in its own
     * Viewport path, bypassing Minecraft's Frustum#isVisible(AABB).
     */
    @ModifyArgs(
        method = "isBoxVisible(IIIFFF)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/caffeinemc/mods/sodium/client/render/viewport/frustum/Frustum;testAab(FFFFFF)Z"
        ),
        require = 0
    )
    private void fov360$expandSodiumSectionBox(Args args) {
        float margin = (float) Fov360Config.getCullingMarginBlocks();
        if (margin <= 0.0F) {
            return;
        }

        args.set(0, ((Float) args.get(0)) - margin);
        args.set(1, ((Float) args.get(1)) - margin);
        args.set(2, ((Float) args.get(2)) - margin);
        args.set(3, ((Float) args.get(3)) + margin);
        args.set(4, ((Float) args.get(4)) + margin);
        args.set(5, ((Float) args.get(5)) + margin);
    }
}
