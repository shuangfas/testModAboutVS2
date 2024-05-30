package com.example.examplemod.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AirCurrent.class,priority = 1500)
public abstract class MixinAirCurrentDestroyer {
    @TargetHandler(
            mixin = "org.valkyrienskies.mod.mixin.mod_compat.create.MixinAirCurrent",
            name = "redirectSetDeltaMovement",
            prefix = "redirect"
    )
    @Inject(
            method = "@MixinSquared:Handler",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void cancel(Entity instance, Vec3 motion, @NotNull CallbackInfo ci) {
        ci.cancel();
    }
}
