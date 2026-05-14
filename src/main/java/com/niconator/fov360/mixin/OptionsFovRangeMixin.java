package com.niconator.fov360.mixin;

import com.mojang.serialization.Codec;
import com.niconator.fov360.Fov360Config;
import com.niconator.fov360.Fov360OptionsAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.io.File;

@Mixin(Options.class)
public class OptionsFovRangeMixin implements Fov360OptionsAccess {
    @Mutable
    @Shadow
    @Final
    private OptionInstance<Integer> fov;

    @ModifyArgs(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/OptionInstance$IntRange;<init>(II)V"
        )
    )
    private void fov360$raiseFovUpperBound(Args args) {
        int min = args.get(0);
        if (min == 30) {
            args.set(1, Fov360Config.getMaxFov());
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void fov360$replaceFovOption(Minecraft minecraft, File gameDirectory, CallbackInfo ci) {
        fov360$rebuildFovOption();
    }

    @Override
    public void fov360$rebuildFovOption() {
        int configuredMax = Fov360Config.getMaxFov();
        int loadedFov = fov360$clamp(this.fov.get(), 30, configuredMax);
        this.fov = new OptionInstance<>(
            "options.fov",
            OptionInstance.noTooltip(),
            OptionsFovRangeMixin::fov360$formatFovLabel,
            new OptionInstance.IntRange(30, configuredMax),
            Codec.DOUBLE.xmap(OptionsFovRangeMixin::fov360$decodeFov, OptionsFovRangeMixin::fov360$encodeFov),
            loadedFov,
            OptionsFovRangeMixin::fov360$onFovChanged
        );
    }

    private static Integer fov360$decodeFov(Double savedValue) {
        return (int) (savedValue * Fov360Config.getFovOptionScale() + 70.0);
    }

    private static Double fov360$encodeFov(Integer fov) {
        return (fov - 70.0) / Fov360Config.getFovOptionScale();
    }

    private static Component fov360$formatFovLabel(Component caption, Integer value) {
        if (value == 70) {
            return Options.genericValueLabel(caption, Component.translatable("options.fov.min"));
        }
        if (value == Fov360Config.getMaxFov()) {
            return Options.genericValueLabel(caption, Component.translatable("options.fov.max"));
        }
        return Options.genericValueLabel(caption, value);
    }

    private static void fov360$onFovChanged(Integer value) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.levelRenderer != null) {
            minecraft.levelRenderer.needsUpdate();
        }
    }

    private static int fov360$clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
